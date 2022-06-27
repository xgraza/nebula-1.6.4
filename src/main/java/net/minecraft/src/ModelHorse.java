package net.minecraft.src;

import org.lwjgl.opengl.GL11;

public class ModelHorse extends ModelBase
{
    private ModelRenderer head;
    private ModelRenderer mouthTop;
    private ModelRenderer mouthBottom;
    private ModelRenderer horseLeftEar;
    private ModelRenderer horseRightEar;
    private ModelRenderer field_110703_f;
    private ModelRenderer field_110704_g;
    private ModelRenderer neck;
    private ModelRenderer field_110717_i;
    private ModelRenderer mane;
    private ModelRenderer body;
    private ModelRenderer tailBase;
    private ModelRenderer tailMiddle;
    private ModelRenderer tailTip;
    private ModelRenderer backLeftLeg;
    private ModelRenderer backLeftShin;
    private ModelRenderer backLeftHoof;
    private ModelRenderer backRightLeg;
    private ModelRenderer backRightShin;
    private ModelRenderer backRightHoof;
    private ModelRenderer frontRightLeg;
    private ModelRenderer frontLeftShin;
    private ModelRenderer frontLeftHoof;
    private ModelRenderer field_110684_D;
    private ModelRenderer frontRightShin;
    private ModelRenderer frontRightHoof;
    private ModelRenderer field_110687_G;
    private ModelRenderer field_110695_H;
    private ModelRenderer field_110696_I;
    private ModelRenderer field_110697_J;
    private ModelRenderer field_110698_K;
    private ModelRenderer field_110691_L;
    private ModelRenderer field_110692_M;
    private ModelRenderer field_110693_N;
    private ModelRenderer field_110694_O;
    private ModelRenderer field_110700_P;
    private ModelRenderer field_110699_Q;
    private ModelRenderer field_110702_R;
    private ModelRenderer field_110701_S;

    public ModelHorse()
    {
        this.textureWidth = 128;
        this.textureHeight = 128;
        this.body = new ModelRenderer(this, 0, 34);
        this.body.addBox(-5.0F, -8.0F, -19.0F, 10, 10, 24);
        this.body.setRotationPoint(0.0F, 11.0F, 9.0F);
        this.tailBase = new ModelRenderer(this, 44, 0);
        this.tailBase.addBox(-1.0F, -1.0F, 0.0F, 2, 2, 3);
        this.tailBase.setRotationPoint(0.0F, 3.0F, 14.0F);
        this.func_110682_a(this.tailBase, -1.134464F, 0.0F, 0.0F);
        this.tailMiddle = new ModelRenderer(this, 38, 7);
        this.tailMiddle.addBox(-1.5F, -2.0F, 3.0F, 3, 4, 7);
        this.tailMiddle.setRotationPoint(0.0F, 3.0F, 14.0F);
        this.func_110682_a(this.tailMiddle, -1.134464F, 0.0F, 0.0F);
        this.tailTip = new ModelRenderer(this, 24, 3);
        this.tailTip.addBox(-1.5F, -4.5F, 9.0F, 3, 4, 7);
        this.tailTip.setRotationPoint(0.0F, 3.0F, 14.0F);
        this.func_110682_a(this.tailTip, -1.40215F, 0.0F, 0.0F);
        this.backLeftLeg = new ModelRenderer(this, 78, 29);
        this.backLeftLeg.addBox(-2.5F, -2.0F, -2.5F, 4, 9, 5);
        this.backLeftLeg.setRotationPoint(4.0F, 9.0F, 11.0F);
        this.backLeftShin = new ModelRenderer(this, 78, 43);
        this.backLeftShin.addBox(-2.0F, 0.0F, -1.5F, 3, 5, 3);
        this.backLeftShin.setRotationPoint(4.0F, 16.0F, 11.0F);
        this.backLeftHoof = new ModelRenderer(this, 78, 51);
        this.backLeftHoof.addBox(-2.5F, 5.1F, -2.0F, 4, 3, 4);
        this.backLeftHoof.setRotationPoint(4.0F, 16.0F, 11.0F);
        this.backRightLeg = new ModelRenderer(this, 96, 29);
        this.backRightLeg.addBox(-1.5F, -2.0F, -2.5F, 4, 9, 5);
        this.backRightLeg.setRotationPoint(-4.0F, 9.0F, 11.0F);
        this.backRightShin = new ModelRenderer(this, 96, 43);
        this.backRightShin.addBox(-1.0F, 0.0F, -1.5F, 3, 5, 3);
        this.backRightShin.setRotationPoint(-4.0F, 16.0F, 11.0F);
        this.backRightHoof = new ModelRenderer(this, 96, 51);
        this.backRightHoof.addBox(-1.5F, 5.1F, -2.0F, 4, 3, 4);
        this.backRightHoof.setRotationPoint(-4.0F, 16.0F, 11.0F);
        this.frontRightLeg = new ModelRenderer(this, 44, 29);
        this.frontRightLeg.addBox(-1.9F, -1.0F, -2.1F, 3, 8, 4);
        this.frontRightLeg.setRotationPoint(4.0F, 9.0F, -8.0F);
        this.frontLeftShin = new ModelRenderer(this, 44, 41);
        this.frontLeftShin.addBox(-1.9F, 0.0F, -1.6F, 3, 5, 3);
        this.frontLeftShin.setRotationPoint(4.0F, 16.0F, -8.0F);
        this.frontLeftHoof = new ModelRenderer(this, 44, 51);
        this.frontLeftHoof.addBox(-2.4F, 5.1F, -2.1F, 4, 3, 4);
        this.frontLeftHoof.setRotationPoint(4.0F, 16.0F, -8.0F);
        this.field_110684_D = new ModelRenderer(this, 60, 29);
        this.field_110684_D.addBox(-1.1F, -1.0F, -2.1F, 3, 8, 4);
        this.field_110684_D.setRotationPoint(-4.0F, 9.0F, -8.0F);
        this.frontRightShin = new ModelRenderer(this, 60, 41);
        this.frontRightShin.addBox(-1.1F, 0.0F, -1.6F, 3, 5, 3);
        this.frontRightShin.setRotationPoint(-4.0F, 16.0F, -8.0F);
        this.frontRightHoof = new ModelRenderer(this, 60, 51);
        this.frontRightHoof.addBox(-1.6F, 5.1F, -2.1F, 4, 3, 4);
        this.frontRightHoof.setRotationPoint(-4.0F, 16.0F, -8.0F);
        this.head = new ModelRenderer(this, 0, 0);
        this.head.addBox(-2.5F, -10.0F, -1.5F, 5, 5, 7);
        this.head.setRotationPoint(0.0F, 4.0F, -10.0F);
        this.func_110682_a(this.head, 0.5235988F, 0.0F, 0.0F);
        this.mouthTop = new ModelRenderer(this, 24, 18);
        this.mouthTop.addBox(-2.0F, -10.0F, -7.0F, 4, 3, 6);
        this.mouthTop.setRotationPoint(0.0F, 3.95F, -10.0F);
        this.func_110682_a(this.mouthTop, 0.5235988F, 0.0F, 0.0F);
        this.mouthBottom = new ModelRenderer(this, 24, 27);
        this.mouthBottom.addBox(-2.0F, -7.0F, -6.5F, 4, 2, 5);
        this.mouthBottom.setRotationPoint(0.0F, 4.0F, -10.0F);
        this.func_110682_a(this.mouthBottom, 0.5235988F, 0.0F, 0.0F);
        this.head.addChild(this.mouthTop);
        this.head.addChild(this.mouthBottom);
        this.horseLeftEar = new ModelRenderer(this, 0, 0);
        this.horseLeftEar.addBox(0.45F, -12.0F, 4.0F, 2, 3, 1);
        this.horseLeftEar.setRotationPoint(0.0F, 4.0F, -10.0F);
        this.func_110682_a(this.horseLeftEar, 0.5235988F, 0.0F, 0.0F);
        this.horseRightEar = new ModelRenderer(this, 0, 0);
        this.horseRightEar.addBox(-2.45F, -12.0F, 4.0F, 2, 3, 1);
        this.horseRightEar.setRotationPoint(0.0F, 4.0F, -10.0F);
        this.func_110682_a(this.horseRightEar, 0.5235988F, 0.0F, 0.0F);
        this.field_110703_f = new ModelRenderer(this, 0, 12);
        this.field_110703_f.addBox(-2.0F, -16.0F, 4.0F, 2, 7, 1);
        this.field_110703_f.setRotationPoint(0.0F, 4.0F, -10.0F);
        this.func_110682_a(this.field_110703_f, 0.5235988F, 0.0F, 0.2617994F);
        this.field_110704_g = new ModelRenderer(this, 0, 12);
        this.field_110704_g.addBox(0.0F, -16.0F, 4.0F, 2, 7, 1);
        this.field_110704_g.setRotationPoint(0.0F, 4.0F, -10.0F);
        this.func_110682_a(this.field_110704_g, 0.5235988F, 0.0F, -0.2617994F);
        this.neck = new ModelRenderer(this, 0, 12);
        this.neck.addBox(-2.05F, -9.8F, -2.0F, 4, 14, 8);
        this.neck.setRotationPoint(0.0F, 4.0F, -10.0F);
        this.func_110682_a(this.neck, 0.5235988F, 0.0F, 0.0F);
        this.field_110687_G = new ModelRenderer(this, 0, 34);
        this.field_110687_G.addBox(-3.0F, 0.0F, 0.0F, 8, 8, 3);
        this.field_110687_G.setRotationPoint(-7.5F, 3.0F, 10.0F);
        this.func_110682_a(this.field_110687_G, 0.0F, ((float)Math.PI / 2F), 0.0F);
        this.field_110695_H = new ModelRenderer(this, 0, 47);
        this.field_110695_H.addBox(-3.0F, 0.0F, 0.0F, 8, 8, 3);
        this.field_110695_H.setRotationPoint(4.5F, 3.0F, 10.0F);
        this.func_110682_a(this.field_110695_H, 0.0F, ((float)Math.PI / 2F), 0.0F);
        this.field_110696_I = new ModelRenderer(this, 80, 0);
        this.field_110696_I.addBox(-5.0F, 0.0F, -3.0F, 10, 1, 8);
        this.field_110696_I.setRotationPoint(0.0F, 2.0F, 2.0F);
        this.field_110697_J = new ModelRenderer(this, 106, 9);
        this.field_110697_J.addBox(-1.5F, -1.0F, -3.0F, 3, 1, 2);
        this.field_110697_J.setRotationPoint(0.0F, 2.0F, 2.0F);
        this.field_110698_K = new ModelRenderer(this, 80, 9);
        this.field_110698_K.addBox(-4.0F, -1.0F, 3.0F, 8, 1, 2);
        this.field_110698_K.setRotationPoint(0.0F, 2.0F, 2.0F);
        this.field_110692_M = new ModelRenderer(this, 74, 0);
        this.field_110692_M.addBox(-0.5F, 6.0F, -1.0F, 1, 2, 2);
        this.field_110692_M.setRotationPoint(5.0F, 3.0F, 2.0F);
        this.field_110691_L = new ModelRenderer(this, 70, 0);
        this.field_110691_L.addBox(-0.5F, 0.0F, -0.5F, 1, 6, 1);
        this.field_110691_L.setRotationPoint(5.0F, 3.0F, 2.0F);
        this.field_110694_O = new ModelRenderer(this, 74, 4);
        this.field_110694_O.addBox(-0.5F, 6.0F, -1.0F, 1, 2, 2);
        this.field_110694_O.setRotationPoint(-5.0F, 3.0F, 2.0F);
        this.field_110693_N = new ModelRenderer(this, 80, 0);
        this.field_110693_N.addBox(-0.5F, 0.0F, -0.5F, 1, 6, 1);
        this.field_110693_N.setRotationPoint(-5.0F, 3.0F, 2.0F);
        this.field_110700_P = new ModelRenderer(this, 74, 13);
        this.field_110700_P.addBox(1.5F, -8.0F, -4.0F, 1, 2, 2);
        this.field_110700_P.setRotationPoint(0.0F, 4.0F, -10.0F);
        this.func_110682_a(this.field_110700_P, 0.5235988F, 0.0F, 0.0F);
        this.field_110699_Q = new ModelRenderer(this, 74, 13);
        this.field_110699_Q.addBox(-2.5F, -8.0F, -4.0F, 1, 2, 2);
        this.field_110699_Q.setRotationPoint(0.0F, 4.0F, -10.0F);
        this.func_110682_a(this.field_110699_Q, 0.5235988F, 0.0F, 0.0F);
        this.field_110702_R = new ModelRenderer(this, 44, 10);
        this.field_110702_R.addBox(2.6F, -6.0F, -6.0F, 0, 3, 16);
        this.field_110702_R.setRotationPoint(0.0F, 4.0F, -10.0F);
        this.field_110701_S = new ModelRenderer(this, 44, 5);
        this.field_110701_S.addBox(-2.6F, -6.0F, -6.0F, 0, 3, 16);
        this.field_110701_S.setRotationPoint(0.0F, 4.0F, -10.0F);
        this.mane = new ModelRenderer(this, 58, 0);
        this.mane.addBox(-1.0F, -11.5F, 5.0F, 2, 16, 4);
        this.mane.setRotationPoint(0.0F, 4.0F, -10.0F);
        this.func_110682_a(this.mane, 0.5235988F, 0.0F, 0.0F);
        this.field_110717_i = new ModelRenderer(this, 80, 12);
        this.field_110717_i.addBox(-2.5F, -10.1F, -7.0F, 5, 5, 12, 0.2F);
        this.field_110717_i.setRotationPoint(0.0F, 4.0F, -10.0F);
        this.func_110682_a(this.field_110717_i, 0.5235988F, 0.0F, 0.0F);
    }

    /**
     * Sets the models various rotation angles then renders the model.
     */
    public void render(Entity par1Entity, float par2, float par3, float par4, float par5, float par6, float par7)
    {
        EntityHorse var8 = (EntityHorse)par1Entity;
        int var9 = var8.getHorseType();
        float var10 = var8.getGrassEatingAmount(0.0F);
        boolean var11 = var8.isAdultHorse();
        boolean var12 = var11 && var8.isHorseSaddled();
        boolean var13 = var11 && var8.isChested();
        boolean var14 = var9 == 1 || var9 == 2;
        float var15 = var8.getHorseSize();
        boolean var16 = var8.riddenByEntity != null;

        if (var12)
        {
            this.field_110717_i.render(par7);
            this.field_110696_I.render(par7);
            this.field_110697_J.render(par7);
            this.field_110698_K.render(par7);
            this.field_110691_L.render(par7);
            this.field_110692_M.render(par7);
            this.field_110693_N.render(par7);
            this.field_110694_O.render(par7);
            this.field_110700_P.render(par7);
            this.field_110699_Q.render(par7);

            if (var16)
            {
                this.field_110702_R.render(par7);
                this.field_110701_S.render(par7);
            }
        }

        if (!var11)
        {
            GL11.glPushMatrix();
            GL11.glScalef(var15, 0.5F + var15 * 0.5F, var15);
            GL11.glTranslatef(0.0F, 0.95F * (1.0F - var15), 0.0F);
        }

        this.backLeftLeg.render(par7);
        this.backLeftShin.render(par7);
        this.backLeftHoof.render(par7);
        this.backRightLeg.render(par7);
        this.backRightShin.render(par7);
        this.backRightHoof.render(par7);
        this.frontRightLeg.render(par7);
        this.frontLeftShin.render(par7);
        this.frontLeftHoof.render(par7);
        this.field_110684_D.render(par7);
        this.frontRightShin.render(par7);
        this.frontRightHoof.render(par7);

        if (!var11)
        {
            GL11.glPopMatrix();
            GL11.glPushMatrix();
            GL11.glScalef(var15, var15, var15);
            GL11.glTranslatef(0.0F, 1.35F * (1.0F - var15), 0.0F);
        }

        this.body.render(par7);
        this.tailBase.render(par7);
        this.tailMiddle.render(par7);
        this.tailTip.render(par7);
        this.neck.render(par7);
        this.mane.render(par7);

        if (!var11)
        {
            GL11.glPopMatrix();
            GL11.glPushMatrix();
            float var17 = 0.5F + var15 * var15 * 0.5F;
            GL11.glScalef(var17, var17, var17);

            if (var10 <= 0.0F)
            {
                GL11.glTranslatef(0.0F, 1.35F * (1.0F - var15), 0.0F);
            }
            else
            {
                GL11.glTranslatef(0.0F, 0.9F * (1.0F - var15) * var10 + 1.35F * (1.0F - var15) * (1.0F - var10), 0.15F * (1.0F - var15) * var10);
            }
        }

        if (var14)
        {
            this.field_110703_f.render(par7);
            this.field_110704_g.render(par7);
        }
        else
        {
            this.horseLeftEar.render(par7);
            this.horseRightEar.render(par7);
        }

        this.head.render(par7);

        if (!var11)
        {
            GL11.glPopMatrix();
        }

        if (var13)
        {
            this.field_110687_G.render(par7);
            this.field_110695_H.render(par7);
        }
    }

    private void func_110682_a(ModelRenderer par1ModelRenderer, float par2, float par3, float par4)
    {
        par1ModelRenderer.rotateAngleX = par2;
        par1ModelRenderer.rotateAngleY = par3;
        par1ModelRenderer.rotateAngleZ = par4;
    }

    private float func_110683_a(float par1, float par2, float par3)
    {
        float var4;

        for (var4 = par2 - par1; var4 < -180.0F; var4 += 360.0F)
        {
            ;
        }

        while (var4 >= 180.0F)
        {
            var4 -= 360.0F;
        }

        return par1 + par3 * var4;
    }

    /**
     * Used for easily adding entity-dependent animations. The second and third float params here are the same second
     * and third as in the setRotationAngles method.
     */
    public void setLivingAnimations(EntityLivingBase par1EntityLivingBase, float par2, float par3, float par4)
    {
        super.setLivingAnimations(par1EntityLivingBase, par2, par3, par4);
        float var5 = this.func_110683_a(par1EntityLivingBase.prevRenderYawOffset, par1EntityLivingBase.renderYawOffset, par4);
        float var6 = this.func_110683_a(par1EntityLivingBase.prevRotationYawHead, par1EntityLivingBase.rotationYawHead, par4);
        float var7 = par1EntityLivingBase.prevRotationPitch + (par1EntityLivingBase.rotationPitch - par1EntityLivingBase.prevRotationPitch) * par4;
        float var8 = var6 - var5;
        float var9 = var7 / (180F / (float)Math.PI);

        if (var8 > 20.0F)
        {
            var8 = 20.0F;
        }

        if (var8 < -20.0F)
        {
            var8 = -20.0F;
        }

        if (par3 > 0.2F)
        {
            var9 += MathHelper.cos(par2 * 0.4F) * 0.15F * par3;
        }

        EntityHorse var10 = (EntityHorse)par1EntityLivingBase;
        float var11 = var10.getGrassEatingAmount(par4);
        float var12 = var10.getRearingAmount(par4);
        float var13 = 1.0F - var12;
        float var14 = var10.func_110201_q(par4);
        boolean var15 = var10.field_110278_bp != 0;
        boolean var16 = var10.isHorseSaddled();
        boolean var17 = var10.riddenByEntity != null;
        float var18 = (float)par1EntityLivingBase.ticksExisted + par4;
        float var19 = MathHelper.cos(par2 * 0.6662F + (float)Math.PI);
        float var20 = var19 * 0.8F * par3;
        this.head.rotationPointY = 4.0F;
        this.head.rotationPointZ = -10.0F;
        this.tailBase.rotationPointY = 3.0F;
        this.tailMiddle.rotationPointZ = 14.0F;
        this.field_110695_H.rotationPointY = 3.0F;
        this.field_110695_H.rotationPointZ = 10.0F;
        this.body.rotateAngleX = 0.0F;
        this.head.rotateAngleX = 0.5235988F + var9;
        this.head.rotateAngleY = var8 / (180F / (float)Math.PI);
        this.head.rotateAngleX = var12 * (0.2617994F + var9) + var11 * 2.18166F + (1.0F - Math.max(var12, var11)) * this.head.rotateAngleX;
        this.head.rotateAngleY = var12 * (var8 / (180F / (float)Math.PI)) + (1.0F - Math.max(var12, var11)) * this.head.rotateAngleY;
        this.head.rotationPointY = var12 * -6.0F + var11 * 11.0F + (1.0F - Math.max(var12, var11)) * this.head.rotationPointY;
        this.head.rotationPointZ = var12 * -1.0F + var11 * -10.0F + (1.0F - Math.max(var12, var11)) * this.head.rotationPointZ;
        this.tailBase.rotationPointY = var12 * 9.0F + var13 * this.tailBase.rotationPointY;
        this.tailMiddle.rotationPointZ = var12 * 18.0F + var13 * this.tailMiddle.rotationPointZ;
        this.field_110695_H.rotationPointY = var12 * 5.5F + var13 * this.field_110695_H.rotationPointY;
        this.field_110695_H.rotationPointZ = var12 * 15.0F + var13 * this.field_110695_H.rotationPointZ;
        this.body.rotateAngleX = var12 * -((float)Math.PI / 4F) + var13 * this.body.rotateAngleX;
        this.horseLeftEar.rotationPointY = this.head.rotationPointY;
        this.horseRightEar.rotationPointY = this.head.rotationPointY;
        this.field_110703_f.rotationPointY = this.head.rotationPointY;
        this.field_110704_g.rotationPointY = this.head.rotationPointY;
        this.neck.rotationPointY = this.head.rotationPointY;
        this.mouthTop.rotationPointY = 0.02F;
        this.mouthBottom.rotationPointY = 0.0F;
        this.mane.rotationPointY = this.head.rotationPointY;
        this.horseLeftEar.rotationPointZ = this.head.rotationPointZ;
        this.horseRightEar.rotationPointZ = this.head.rotationPointZ;
        this.field_110703_f.rotationPointZ = this.head.rotationPointZ;
        this.field_110704_g.rotationPointZ = this.head.rotationPointZ;
        this.neck.rotationPointZ = this.head.rotationPointZ;
        this.mouthTop.rotationPointZ = 0.02F - var14 * 1.0F;
        this.mouthBottom.rotationPointZ = 0.0F + var14 * 1.0F;
        this.mane.rotationPointZ = this.head.rotationPointZ;
        this.horseLeftEar.rotateAngleX = this.head.rotateAngleX;
        this.horseRightEar.rotateAngleX = this.head.rotateAngleX;
        this.field_110703_f.rotateAngleX = this.head.rotateAngleX;
        this.field_110704_g.rotateAngleX = this.head.rotateAngleX;
        this.neck.rotateAngleX = this.head.rotateAngleX;
        this.mouthTop.rotateAngleX = 0.0F - 0.09424778F * var14;
        this.mouthBottom.rotateAngleX = 0.0F + 0.15707964F * var14;
        this.mane.rotateAngleX = this.head.rotateAngleX;
        this.horseLeftEar.rotateAngleY = this.head.rotateAngleY;
        this.horseRightEar.rotateAngleY = this.head.rotateAngleY;
        this.field_110703_f.rotateAngleY = this.head.rotateAngleY;
        this.field_110704_g.rotateAngleY = this.head.rotateAngleY;
        this.neck.rotateAngleY = this.head.rotateAngleY;
        this.mouthTop.rotateAngleY = 0.0F;
        this.mouthBottom.rotateAngleY = 0.0F;
        this.mane.rotateAngleY = this.head.rotateAngleY;
        this.field_110687_G.rotateAngleX = var20 / 5.0F;
        this.field_110695_H.rotateAngleX = -var20 / 5.0F;
        float var21 = ((float)Math.PI / 2F);
        float var22 = ((float)Math.PI * 3F / 2F);
        float var23 = -1.0471976F;
        float var24 = 0.2617994F * var12;
        float var25 = MathHelper.cos(var18 * 0.6F + (float)Math.PI);
        this.frontRightLeg.rotationPointY = -2.0F * var12 + 9.0F * var13;
        this.frontRightLeg.rotationPointZ = -2.0F * var12 + -8.0F * var13;
        this.field_110684_D.rotationPointY = this.frontRightLeg.rotationPointY;
        this.field_110684_D.rotationPointZ = this.frontRightLeg.rotationPointZ;
        this.backLeftShin.rotationPointY = this.backLeftLeg.rotationPointY + MathHelper.sin(((float)Math.PI / 2F) + var24 + var13 * -var19 * 0.5F * par3) * 7.0F;
        this.backLeftShin.rotationPointZ = this.backLeftLeg.rotationPointZ + MathHelper.cos(((float)Math.PI * 3F / 2F) + var24 + var13 * -var19 * 0.5F * par3) * 7.0F;
        this.backRightShin.rotationPointY = this.backRightLeg.rotationPointY + MathHelper.sin(((float)Math.PI / 2F) + var24 + var13 * var19 * 0.5F * par3) * 7.0F;
        this.backRightShin.rotationPointZ = this.backRightLeg.rotationPointZ + MathHelper.cos(((float)Math.PI * 3F / 2F) + var24 + var13 * var19 * 0.5F * par3) * 7.0F;
        float var26 = (-1.0471976F + var25) * var12 + var20 * var13;
        float var27 = (-1.0471976F + -var25) * var12 + -var20 * var13;
        this.frontLeftShin.rotationPointY = this.frontRightLeg.rotationPointY + MathHelper.sin(((float)Math.PI / 2F) + var26) * 7.0F;
        this.frontLeftShin.rotationPointZ = this.frontRightLeg.rotationPointZ + MathHelper.cos(((float)Math.PI * 3F / 2F) + var26) * 7.0F;
        this.frontRightShin.rotationPointY = this.field_110684_D.rotationPointY + MathHelper.sin(((float)Math.PI / 2F) + var27) * 7.0F;
        this.frontRightShin.rotationPointZ = this.field_110684_D.rotationPointZ + MathHelper.cos(((float)Math.PI * 3F / 2F) + var27) * 7.0F;
        this.backLeftLeg.rotateAngleX = var24 + -var19 * 0.5F * par3 * var13;
        this.backLeftShin.rotateAngleX = -0.08726646F * var12 + (-var19 * 0.5F * par3 - Math.max(0.0F, var19 * 0.5F * par3)) * var13;
        this.backLeftHoof.rotateAngleX = this.backLeftShin.rotateAngleX;
        this.backRightLeg.rotateAngleX = var24 + var19 * 0.5F * par3 * var13;
        this.backRightShin.rotateAngleX = -0.08726646F * var12 + (var19 * 0.5F * par3 - Math.max(0.0F, -var19 * 0.5F * par3)) * var13;
        this.backRightHoof.rotateAngleX = this.backRightShin.rotateAngleX;
        this.frontRightLeg.rotateAngleX = var26;
        this.frontLeftShin.rotateAngleX = (this.frontRightLeg.rotateAngleX + (float)Math.PI * Math.max(0.0F, 0.2F + var25 * 0.2F)) * var12 + (var20 + Math.max(0.0F, var19 * 0.5F * par3)) * var13;
        this.frontLeftHoof.rotateAngleX = this.frontLeftShin.rotateAngleX;
        this.field_110684_D.rotateAngleX = var27;
        this.frontRightShin.rotateAngleX = (this.field_110684_D.rotateAngleX + (float)Math.PI * Math.max(0.0F, 0.2F - var25 * 0.2F)) * var12 + (-var20 + Math.max(0.0F, -var19 * 0.5F * par3)) * var13;
        this.frontRightHoof.rotateAngleX = this.frontRightShin.rotateAngleX;
        this.backLeftHoof.rotationPointY = this.backLeftShin.rotationPointY;
        this.backLeftHoof.rotationPointZ = this.backLeftShin.rotationPointZ;
        this.backRightHoof.rotationPointY = this.backRightShin.rotationPointY;
        this.backRightHoof.rotationPointZ = this.backRightShin.rotationPointZ;
        this.frontLeftHoof.rotationPointY = this.frontLeftShin.rotationPointY;
        this.frontLeftHoof.rotationPointZ = this.frontLeftShin.rotationPointZ;
        this.frontRightHoof.rotationPointY = this.frontRightShin.rotationPointY;
        this.frontRightHoof.rotationPointZ = this.frontRightShin.rotationPointZ;

        if (var16)
        {
            this.field_110696_I.rotationPointY = var12 * 0.5F + var13 * 2.0F;
            this.field_110696_I.rotationPointZ = var12 * 11.0F + var13 * 2.0F;
            this.field_110697_J.rotationPointY = this.field_110696_I.rotationPointY;
            this.field_110698_K.rotationPointY = this.field_110696_I.rotationPointY;
            this.field_110691_L.rotationPointY = this.field_110696_I.rotationPointY;
            this.field_110693_N.rotationPointY = this.field_110696_I.rotationPointY;
            this.field_110692_M.rotationPointY = this.field_110696_I.rotationPointY;
            this.field_110694_O.rotationPointY = this.field_110696_I.rotationPointY;
            this.field_110687_G.rotationPointY = this.field_110695_H.rotationPointY;
            this.field_110697_J.rotationPointZ = this.field_110696_I.rotationPointZ;
            this.field_110698_K.rotationPointZ = this.field_110696_I.rotationPointZ;
            this.field_110691_L.rotationPointZ = this.field_110696_I.rotationPointZ;
            this.field_110693_N.rotationPointZ = this.field_110696_I.rotationPointZ;
            this.field_110692_M.rotationPointZ = this.field_110696_I.rotationPointZ;
            this.field_110694_O.rotationPointZ = this.field_110696_I.rotationPointZ;
            this.field_110687_G.rotationPointZ = this.field_110695_H.rotationPointZ;
            this.field_110696_I.rotateAngleX = this.body.rotateAngleX;
            this.field_110697_J.rotateAngleX = this.body.rotateAngleX;
            this.field_110698_K.rotateAngleX = this.body.rotateAngleX;
            this.field_110702_R.rotationPointY = this.head.rotationPointY;
            this.field_110701_S.rotationPointY = this.head.rotationPointY;
            this.field_110717_i.rotationPointY = this.head.rotationPointY;
            this.field_110700_P.rotationPointY = this.head.rotationPointY;
            this.field_110699_Q.rotationPointY = this.head.rotationPointY;
            this.field_110702_R.rotationPointZ = this.head.rotationPointZ;
            this.field_110701_S.rotationPointZ = this.head.rotationPointZ;
            this.field_110717_i.rotationPointZ = this.head.rotationPointZ;
            this.field_110700_P.rotationPointZ = this.head.rotationPointZ;
            this.field_110699_Q.rotationPointZ = this.head.rotationPointZ;
            this.field_110702_R.rotateAngleX = var9;
            this.field_110701_S.rotateAngleX = var9;
            this.field_110717_i.rotateAngleX = this.head.rotateAngleX;
            this.field_110700_P.rotateAngleX = this.head.rotateAngleX;
            this.field_110699_Q.rotateAngleX = this.head.rotateAngleX;
            this.field_110717_i.rotateAngleY = this.head.rotateAngleY;
            this.field_110700_P.rotateAngleY = this.head.rotateAngleY;
            this.field_110702_R.rotateAngleY = this.head.rotateAngleY;
            this.field_110699_Q.rotateAngleY = this.head.rotateAngleY;
            this.field_110701_S.rotateAngleY = this.head.rotateAngleY;

            if (var17)
            {
                this.field_110691_L.rotateAngleX = -1.0471976F;
                this.field_110692_M.rotateAngleX = -1.0471976F;
                this.field_110693_N.rotateAngleX = -1.0471976F;
                this.field_110694_O.rotateAngleX = -1.0471976F;
                this.field_110691_L.rotateAngleZ = 0.0F;
                this.field_110692_M.rotateAngleZ = 0.0F;
                this.field_110693_N.rotateAngleZ = 0.0F;
                this.field_110694_O.rotateAngleZ = 0.0F;
            }
            else
            {
                this.field_110691_L.rotateAngleX = var20 / 3.0F;
                this.field_110692_M.rotateAngleX = var20 / 3.0F;
                this.field_110693_N.rotateAngleX = var20 / 3.0F;
                this.field_110694_O.rotateAngleX = var20 / 3.0F;
                this.field_110691_L.rotateAngleZ = var20 / 5.0F;
                this.field_110692_M.rotateAngleZ = var20 / 5.0F;
                this.field_110693_N.rotateAngleZ = -var20 / 5.0F;
                this.field_110694_O.rotateAngleZ = -var20 / 5.0F;
            }
        }

        var21 = -1.3089F + par3 * 1.5F;

        if (var21 > 0.0F)
        {
            var21 = 0.0F;
        }

        if (var15)
        {
            this.tailBase.rotateAngleY = MathHelper.cos(var18 * 0.7F);
            var21 = 0.0F;
        }
        else
        {
            this.tailBase.rotateAngleY = 0.0F;
        }

        this.tailMiddle.rotateAngleY = this.tailBase.rotateAngleY;
        this.tailTip.rotateAngleY = this.tailBase.rotateAngleY;
        this.tailMiddle.rotationPointY = this.tailBase.rotationPointY;
        this.tailTip.rotationPointY = this.tailBase.rotationPointY;
        this.tailMiddle.rotationPointZ = this.tailBase.rotationPointZ;
        this.tailTip.rotationPointZ = this.tailBase.rotationPointZ;
        this.tailBase.rotateAngleX = var21;
        this.tailMiddle.rotateAngleX = var21;
        this.tailTip.rotateAngleX = -0.2618F + var21;
    }
}
