package lol.nebula.util.render.shader;

import lol.nebula.Nebula;
import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.ARBFragmentShader;
import org.lwjgl.opengl.ARBShaderObjects;
import org.lwjgl.opengl.ARBVertexShader;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.lwjgl.opengl.ARBTessellationShader.GL_FALSE;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;

/**
 * <a href="http://web.archive.org/web/20200703200808/http://wiki.lwjgl.org/wiki/GLSL_Shaders_with_LWJGL.html">...</a>
 */
public abstract class Shader {

    /**
     * The minecraft game instance
     */
    protected final Minecraft mc = Minecraft.getMinecraft();

    private final Map<String, Integer> uniforms = new LinkedHashMap<>();

    protected int program;
    protected boolean canUse = false;

    public Shader(String frag) {
        this(frag, "/assets/minecraft/nebula/shaders/vertex.vsh");
    }

    public Shader(String frag, String vert) {
        int fragShader = 0;
        int vertShader = 0;

        try {
            vertShader = compile(vert, ARBVertexShader.GL_VERTEX_SHADER_ARB);
            fragShader = compile(frag, ARBFragmentShader.GL_FRAGMENT_SHADER_ARB);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fragShader == 0 || vertShader == 0) {
                Nebula.getLogger().error("Failed to compile shaders!");
                return;
            }
        }

        program = ARBShaderObjects.glCreateProgramObjectARB();
        if (program == 0) {
            return;
        }

        ARBShaderObjects.glAttachObjectARB(program, vertShader);
        ARBShaderObjects.glAttachObjectARB(program, fragShader);

        ARBShaderObjects.glLinkProgramARB(program);
        if (ARBShaderObjects.glGetObjectParameteriARB(program, ARBShaderObjects.GL_OBJECT_LINK_STATUS_ARB) == GL_FALSE) {
            Nebula.getLogger().error(getLogInfo(program));
            return;
        }

        ARBShaderObjects.glValidateProgramARB(program);
        if (ARBShaderObjects.glGetObjectParameteriARB(program, ARBShaderObjects.GL_OBJECT_VALIDATE_STATUS_ARB) == GL_FALSE) {
            Nebula.getLogger().error(getLogInfo(program));
            return;
        }

        canUse = true;
        onInitialize();
    }

    protected abstract void onInitialize();

    public void use() {
        if (canUse) {
            ARBShaderObjects.glUseProgramObjectARB(program);
        }
    }

    public void stopUse() {
        ARBShaderObjects.glUseProgramObjectARB(0);
    }

    public void createUniform(String name) {
        uniforms.put(name, glGetUniformLocation(program, name));
    }

    public int getUniform(String name) {
        return uniforms.getOrDefault(name, -1);
    }

    public static String getLogInfo(int obj) {
        return ARBShaderObjects.glGetInfoLogARB(obj, ARBShaderObjects.glGetObjectParameteriARB(obj, ARBShaderObjects.GL_OBJECT_INFO_LOG_LENGTH_ARB));
    }

    public static int compile(String loc, int type) {
        InputStream stream = Shader.class.getResourceAsStream(loc);
        if (stream == null) {
            return 0;
        }

        StringBuilder content = new StringBuilder();
        try {
            int i;
            while ((i = stream.read()) != -1) {
                content.append((char) i);
            }
        } catch (IOException ignored) {
            return 0;
        }

        int shader = 0;

        try {
            shader = ARBShaderObjects.glCreateShaderObjectARB(type);
            if (shader == 0) {
                return 0;
            }

            ARBShaderObjects.glShaderSourceARB(shader, content.toString());
            ARBShaderObjects.glCompileShaderARB(shader);
        } catch (Exception e) {
            ARBShaderObjects.glDeleteObjectARB(shader);
            e.printStackTrace();
        }

        return shader;
    }
}