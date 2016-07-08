package com.easyliu.demo.orientaionsensor.activity;

/**
 * Created by v_easyliu on 2016/7/8.
 */
public class MahonyAHRS {
    public float SamplePeriod;
    public float Kp;
    public float Ki;
    public float[] mQuaternion;
    private float[] eInt;
    private float[] mAcc = new float[3];
    private float[] mGyro = new float[3];
    private float[] mMag = new float[3];

    public MahonyAHRS(float samplePeriod) {
        this(samplePeriod, 1f, 0f);
    }

    public MahonyAHRS(float samplePeriod, float kp) {
        this(samplePeriod, kp, 0f);
    }

    public MahonyAHRS(float samplePeriod, float kp, float ki) {
        SamplePeriod = samplePeriod;
        Kp = kp;
        Ki = ki;
        mQuaternion = new float[]{1f, 0f, 0f, 0f};
        eInt = new float[]{0f, 0f, 0f};
    }

    /**
     * 得到姿态角
     *
     * @param angles
     */
    public void getYawPitchRoll(float[] acc, float[] gyro, float[] mag, float[] angles) {
        mAcc = acc;
        mGyro = gyro;
        mMag = mag;
        Update(mGyro[0], mGyro[1], mGyro[2], mAcc[0], mAcc[1], mAcc[2], mMag[0], mMag[1], mMag[2]);
        //更新全局四元数
        angles[0] = (float) (-Math.atan2(2 * mQuaternion[1] * mQuaternion[2] - 2 * mQuaternion[0] * mQuaternion[3], -2 * mQuaternion[1] * mQuaternion[1] - 2 * mQuaternion[3] * mQuaternion[3] + 1) * 57.295780);  //偏航角，绕z轴转动
        angles[1] = (float) (Math.asin(2 * mQuaternion[2] * mQuaternion[3] + 2 * mQuaternion[0] * mQuaternion[1]) * 57.295780); //俯仰角，绕x轴转动
        angles[2] = (float) (-Math.atan2(-2 * mQuaternion[0] * mQuaternion[2] + 2 * mQuaternion[1] * mQuaternion[3], -2 * mQuaternion[1] * mQuaternion[1] - 2 * mQuaternion[2] * mQuaternion[2] + 1) * 57.295780); //滚动角，绕y轴转动
    }

    /**
     * 更新四元数
     *
     * @param gx
     * @param gy
     * @param gz
     * @param ax
     * @param ay
     * @param az
     * @param mx
     * @param my
     * @param mz
     */
    public void Update(float gx, float gy, float gz, float ax, float ay, float az, float mx, float my, float mz) {
        float q1 = mQuaternion[0], q2 = mQuaternion[1], q3 = mQuaternion[2], q4 = mQuaternion[3];   // short name local variable for readability
        float norm;
        float hx, hy, bx, bz;
        float vx, vy, vz, wx, wy, wz;
        float ex, ey, ez;
        float pa, pb, pc;

        // Auxiliary variables to avoid repeated arithmetic
        float q1q1 = q1 * q1;
        float q1q2 = q1 * q2;
        float q1q3 = q1 * q3;
        float q1q4 = q1 * q4;
        float q2q2 = q2 * q2;
        float q2q3 = q2 * q3;
        float q2q4 = q2 * q4;
        float q3q3 = q3 * q3;
        float q3q4 = q3 * q4;
        float q4q4 = q4 * q4;

        // Normalise accelerometer measurement
        norm = (float) Math.sqrt(ax * ax + ay * ay + az * az);
        if (norm == 0f) return; // handle NaN
        norm = 1 / norm;        // use reciprocal for division
        ax *= norm;
        ay *= norm;
        az *= norm;

        // Normalise magnetometer measurement
        norm = (float) Math.sqrt(mx * mx + my * my + mz * mz);
        if (norm == 0f) return; // handle NaN
        norm = 1 / norm;        // use reciprocal for division
        mx *= norm;
        my *= norm;
        mz *= norm;

        // Reference direction of Earth's magnetic field
        hx = 2f * mx * (0.5f - q3q3 - q4q4) + 2f * my * (q2q3 - q1q4) + 2f * mz * (q2q4 + q1q3);
        hy = 2f * mx * (q2q3 + q1q4) + 2f * my * (0.5f - q2q2 - q4q4) + 2f * mz * (q3q4 - q1q2);
        bx = (float) Math.sqrt((hx * hx) + (hy * hy));
        bz = 2f * mx * (q2q4 - q1q3) + 2f * my * (q3q4 + q1q2) + 2f * mz * (0.5f - q2q2 - q3q3);

        // Estimated direction of gravity and magnetic field
        vx = 2f * (q2q4 - q1q3);
        vy = 2f * (q1q2 + q3q4);
        vz = q1q1 - q2q2 - q3q3 + q4q4;
        wx = 2f * bx * (0.5f - q3q3 - q4q4) + 2f * bz * (q2q4 - q1q3);
        wy = 2f * bx * (q2q3 - q1q4) + 2f * bz * (q1q2 + q3q4);
        wz = 2f * bx * (q1q3 + q2q4) + 2f * bz * (0.5f - q2q2 - q3q3);

        // Error is cross product between estimated direction and measured direction of gravity
        ex = (ay * vz - az * vy) + (my * wz - mz * wy);
        ey = (az * vx - ax * vz) + (mz * wx - mx * wz);
        ez = (ax * vy - ay * vx) + (mx * wy - my * wx);
        if (Ki > 0f) {
            eInt[0] += ex;      // accumulate integral error
            eInt[1] += ey;
            eInt[2] += ez;
        } else {
            eInt[0] = 0.0f;     // prevent integral wind up
            eInt[1] = 0.0f;
            eInt[2] = 0.0f;
        }

        // Apply feedback terms
        gx = gx + Kp * ex + Ki * eInt[0];
        gy = gy + Kp * ey + Ki * eInt[1];
        gz = gz + Kp * ez + Ki * eInt[2];

        // Integrate rate of change of quaternion
        pa = q2;
        pb = q3;
        pc = q4;
        q1 = q1 + (-q2 * gx - q3 * gy - q4 * gz) * (0.5f * SamplePeriod);
        q2 = pa + (q1 * gx + pb * gz - pc * gy) * (0.5f * SamplePeriod);
        q3 = pb + (q1 * gy - pa * gz + pc * gx) * (0.5f * SamplePeriod);
        q4 = pc + (q1 * gz + pa * gy - pb * gx) * (0.5f * SamplePeriod);

        // Normalise quaternion
        norm = (float) Math.sqrt(q1 * q1 + q2 * q2 + q3 * q3 + q4 * q4);
        norm = 1.0f / norm;
        mQuaternion[0] = q1 * norm;
        mQuaternion[1] = q2 * norm;
        mQuaternion[2] = q3 * norm;
        mQuaternion[3] = q4 * norm;
    }

    /**
     * 更新四元数
     *
     * @param gx
     * @param gy
     * @param gz
     * @param ax
     * @param ay
     * @param az
     */
    public void Update(float gx, float gy, float gz, float ax, float ay, float az) {
        float q1 = mQuaternion[0], q2 = mQuaternion[1], q3 = mQuaternion[2], q4 = mQuaternion[3];   // short name local variable for readability
        float norm;
        float vx, vy, vz;
        float ex, ey, ez;
        float pa, pb, pc;

        // Normalise accelerometer measurement
        norm = (float) Math.sqrt(ax * ax + ay * ay + az * az);
        if (norm == 0f) return; // handle NaN
        norm = 1 / norm;        // use reciprocal for division
        ax *= norm;
        ay *= norm;
        az *= norm;

        // Estimated direction of gravity
        vx = 2.0f * (q2 * q4 - q1 * q3);
        vy = 2.0f * (q1 * q2 + q3 * q4);
        vz = q1 * q1 - q2 * q2 - q3 * q3 + q4 * q4;

        // Error is cross product between estimated direction and measured direction of gravity
        ex = (ay * vz - az * vy);
        ey = (az * vx - ax * vz);
        ez = (ax * vy - ay * vx);
        if (Ki > 0f) {
            eInt[0] += ex;      // accumulate integral error
            eInt[1] += ey;
            eInt[2] += ez;
        } else {
            eInt[0] = 0.0f;     // prevent integral wind up
            eInt[1] = 0.0f;
            eInt[2] = 0.0f;
        }

        // Apply feedback terms
        gx = gx + Kp * ex + Ki * eInt[0];
        gy = gy + Kp * ey + Ki * eInt[1];
        gz = gz + Kp * ez + Ki * eInt[2];

        // Integrate rate of change of quaternion
        pa = q2;
        pb = q3;
        pc = q4;
        q1 = q1 + (-q2 * gx - q3 * gy - q4 * gz) * (0.5f * SamplePeriod);
        q2 = pa + (q1 * gx + pb * gz - pc * gy) * (0.5f * SamplePeriod);
        q3 = pb + (q1 * gy - pa * gz + pc * gx) * (0.5f * SamplePeriod);
        q4 = pc + (q1 * gz + pa * gy - pb * gx) * (0.5f * SamplePeriod);

        // Normalise quaternion
        norm = (float) Math.sqrt(q1 * q1 + q2 * q2 + q3 * q3 + q4 * q4);
        norm = 1.0f / norm;
        mQuaternion[0] = q1 * norm;
        mQuaternion[1] = q2 * norm;
        mQuaternion[2] = q3 * norm;
        mQuaternion[3] = q4 * norm;
    }
}


