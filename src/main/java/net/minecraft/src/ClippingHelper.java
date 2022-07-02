package net.minecraft.src;

public class ClippingHelper
{
    public float[][] frustum = new float[16][16];
    public float[] projectionMatrix = new float[16];
    public float[] modelviewMatrix = new float[16];
    public float[] clippingMatrix = new float[16];

    /**
     * Returns true if the box is inside all 6 clipping planes, otherwise returns false.
     */
    public boolean isBoxInFrustum(double par1, double par3, double par5, double par7, double par9, double par11)
    {
        for (int var13 = 0; var13 < 6; ++var13)
        {
            float var14 = (float)par1;
            float var15 = (float)par3;
            float var16 = (float)par5;
            float var17 = (float)par7;
            float var18 = (float)par9;
            float var19 = (float)par11;

            if (this.frustum[var13][0] * var14 + this.frustum[var13][1] * var15 + this.frustum[var13][2] * var16 + this.frustum[var13][3] <= 0.0F && this.frustum[var13][0] * var17 + this.frustum[var13][1] * var15 + this.frustum[var13][2] * var16 + this.frustum[var13][3] <= 0.0F && this.frustum[var13][0] * var14 + this.frustum[var13][1] * var18 + this.frustum[var13][2] * var16 + this.frustum[var13][3] <= 0.0F && this.frustum[var13][0] * var17 + this.frustum[var13][1] * var18 + this.frustum[var13][2] * var16 + this.frustum[var13][3] <= 0.0F && this.frustum[var13][0] * var14 + this.frustum[var13][1] * var15 + this.frustum[var13][2] * var19 + this.frustum[var13][3] <= 0.0F && this.frustum[var13][0] * var17 + this.frustum[var13][1] * var15 + this.frustum[var13][2] * var19 + this.frustum[var13][3] <= 0.0F && this.frustum[var13][0] * var14 + this.frustum[var13][1] * var18 + this.frustum[var13][2] * var19 + this.frustum[var13][3] <= 0.0F && this.frustum[var13][0] * var17 + this.frustum[var13][1] * var18 + this.frustum[var13][2] * var19 + this.frustum[var13][3] <= 0.0F)
            {
                return false;
            }
        }

        return true;
    }

    public boolean isBoxInFrustumFully(double minX, double minY, double minZ, double maxX, double maxY, double maxZ)
    {
        for (int i = 0; i < 6; ++i)
        {
            float minXf = (float)minX;
            float minYf = (float)minY;
            float minZf = (float)minZ;
            float maxXf = (float)maxX;
            float maxYf = (float)maxY;
            float maxZf = (float)maxZ;

            if (i < 4)
            {
                if (this.frustum[i][0] * minXf + this.frustum[i][1] * minYf + this.frustum[i][2] * minZf + this.frustum[i][3] <= 0.0F || this.frustum[i][0] * maxXf + this.frustum[i][1] * minYf + this.frustum[i][2] * minZf + this.frustum[i][3] <= 0.0F || this.frustum[i][0] * minXf + this.frustum[i][1] * maxYf + this.frustum[i][2] * minZf + this.frustum[i][3] <= 0.0F || this.frustum[i][0] * maxXf + this.frustum[i][1] * maxYf + this.frustum[i][2] * minZf + this.frustum[i][3] <= 0.0F || this.frustum[i][0] * minXf + this.frustum[i][1] * minYf + this.frustum[i][2] * maxZf + this.frustum[i][3] <= 0.0F || this.frustum[i][0] * maxXf + this.frustum[i][1] * minYf + this.frustum[i][2] * maxZf + this.frustum[i][3] <= 0.0F || this.frustum[i][0] * minXf + this.frustum[i][1] * maxYf + this.frustum[i][2] * maxZf + this.frustum[i][3] <= 0.0F || this.frustum[i][0] * maxXf + this.frustum[i][1] * maxYf + this.frustum[i][2] * maxZf + this.frustum[i][3] <= 0.0F)
                {
                    return false;
                }
            }
            else if (this.frustum[i][0] * minXf + this.frustum[i][1] * minYf + this.frustum[i][2] * minZf + this.frustum[i][3] <= 0.0F && this.frustum[i][0] * maxXf + this.frustum[i][1] * minYf + this.frustum[i][2] * minZf + this.frustum[i][3] <= 0.0F && this.frustum[i][0] * minXf + this.frustum[i][1] * maxYf + this.frustum[i][2] * minZf + this.frustum[i][3] <= 0.0F && this.frustum[i][0] * maxXf + this.frustum[i][1] * maxYf + this.frustum[i][2] * minZf + this.frustum[i][3] <= 0.0F && this.frustum[i][0] * minXf + this.frustum[i][1] * minYf + this.frustum[i][2] * maxZf + this.frustum[i][3] <= 0.0F && this.frustum[i][0] * maxXf + this.frustum[i][1] * minYf + this.frustum[i][2] * maxZf + this.frustum[i][3] <= 0.0F && this.frustum[i][0] * minXf + this.frustum[i][1] * maxYf + this.frustum[i][2] * maxZf + this.frustum[i][3] <= 0.0F && this.frustum[i][0] * maxXf + this.frustum[i][1] * maxYf + this.frustum[i][2] * maxZf + this.frustum[i][3] <= 0.0F)
            {
                return false;
            }
        }

        return true;
    }
}
