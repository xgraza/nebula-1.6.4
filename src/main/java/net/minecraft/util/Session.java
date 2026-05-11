package net.minecraft.util;

import com.mojang.authlib.GameProfile;

public class Session
{
    private final String username;
    private final String playerID;
    private final String token;

    public Session(String username, String playerID, String token)
    {
        this.username = username;
        this.playerID = playerID;
        this.token = token;
    }

    public String getSessionID()
    {
        return "token:" + this.token + ":" + this.playerID;
    }

    public String getPlayerID()
    {
        return this.playerID;
    }

    public String getUsername()
    {
        return this.username;
    }

    public String getToken()
    {
        return this.token;
    }

    public GameProfile getGameProfile()
    {
        return new GameProfile(this.getPlayerID(), this.getUsername());
    }
}
