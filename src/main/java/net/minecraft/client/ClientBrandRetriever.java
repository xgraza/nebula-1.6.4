package net.minecraft.client;

import yzy.szn.launcher.YZYBootstrap;

public class ClientBrandRetriever
{
    private static final String __OBFID = "CL_00001460";

    public static String getClientModName()
    {
        return YZYBootstrap.getClientBrand();
    }
}
