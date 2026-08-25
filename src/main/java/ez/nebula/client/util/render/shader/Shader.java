package ez.nebula.client.util.render.shader;

import ez.nebula.client.core.Nebula;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL20.*;

/**
 * @author aesthetical
 * @since 06/12/23
 */
public final class Shader
{
    /**
     * A default program ID for no shader specified
     */
    private static final int NO_SHADER = 0;

    private final int programId;
    private final Map<String, Integer> uniforms = new HashMap<>();

    /**
     * Compiles, attaches, and links a shader program
     *
     * @param vertex   the location of the vertex shader
     * @param fragment the location of the fragment shader
     * @param runnable a consumer with the local {@link Shader} object to create uniforms
     */
    public Shader(final String vertex,
                  final String fragment,
                  final Consumer<Shader> runnable)
    {
        final int program = glCreateProgram();

        final int vertShader = compileShader(vertex, GL_VERTEX_SHADER);
        final int fragShader = compileShader(fragment, GL_FRAGMENT_SHADER);
        if (vertShader == 0)
        {
            throw new RuntimeException("Vertex shader has not compiled correctly.");
        }
        if (fragShader == 0)
        {
            throw new RuntimeException("Fragment shader has not compiled correctly.");
        }

        glAttachShader(program, vertShader);
        glAttachShader(program, fragShader);
        glLinkProgram(program);

        if (glGetShaderi(program, GL_LINK_STATUS) == GL_FALSE)
        {
            throw new RuntimeException("Failed to link shader: "
                    + glGetProgramInfoLog(program, glGetShaderi(program, GL_INFO_LOG_LENGTH)));
        }

        programId = program;
        if (runnable != null)
        {
            createUniforms(runnable);
        }
    }

    /**
     * Creates and caches uniforms supplied by the runnable
     *
     * @param runnable a runnable containing the local {@link Shader} object
     */
    public void createUniforms(final Consumer<Shader> runnable)
    {
        use();
        runnable.accept(this);
        stop();
    }

    /**
     * Uses this shader
     *
     * @see org.lwjgl.opengl.GL20#glUseProgram(int)
     */
    public void use()
    {
        if (!glIsProgram(programId))
        {
            throw new RuntimeException(programId + " has been deleted");
        }
        glUseProgram(programId);
    }

    /**
     * Uses this shader
     *
     * @param runnable the runnable
     * @see org.lwjgl.opengl.GL20#glUseProgram(int)
     * @see #use(Runnable)
     */
    public void use(final Runnable runnable)
    {
        use();
        runnable.run();
    }

    /**
     * Stops using this shader
     *
     * @see org.lwjgl.opengl.GL20#glUseProgram(int)
     */
    public void stop()
    {
        glUseProgram(NO_SHADER);
    }

    /**
     * Creates and caches a uniform
     *
     * @param name the uniform name
     */
    public void createUniform(final String name)
    {
        if (uniforms.containsKey(name))
        {
            return;
        }
        uniforms.put(name, glGetUniformLocation(programId, name));
    }

    /**
     * Sets a uniform value
     *
     * @param name   the uniform name
     * @param values the value(s) to pipe into the uniform
     */
    public void set(final String name, final int... values)
    {
        final int location = uniforms.getOrDefault(name, -1);
        if (location == -1)
        {
            return;
        }

        switch (values.length)
        {
            case 1:
                glUniform1i(location, values[0]);
                break;

            case 2:
                glUniform2i(location, values[0], values[1]);
                break;

            case 3:
                glUniform3i(location, values[0], values[1], values[2]);
                break;

            case 4:
                glUniform4i(location, values[0], values[1], values[2], values[3]);
                break;
        }
    }

    /**
     * Sets a uniform value
     *
     * @param name   the uniform name
     * @param values the value(s) to pipe into the uniform
     */
    public void set(final String name, final float... values)
    {
        final int location = uniforms.getOrDefault(name, -1);
        if (location == -1)
        {
            return;
        }

        switch (values.length)
        {
            case 1:
                glUniform1f(location, values[0]);
                break;

            case 2:
                glUniform2f(location, values[0], values[1]);
                break;

            case 3:
                glUniform3f(location, values[0], values[1], values[2]);
                break;

            case 4:
                glUniform4f(location, values[0], values[1], values[2], values[3]);
                break;
        }
    }

    /**
     * Compiles a shader
     *
     * @param location   the shader location
     * @param shaderType the shader type
     * @return the resulting shader ID or 0
     */
    private int compileShader(final String location, final int shaderType)
    {
        String content;
        try (final InputStream stream = Shader.class.getResourceAsStream(location))
        {
            if (stream == null)
            {
                return NO_SHADER;
            }
            content = String.join("\n", IOUtils.readLines(stream));
            if (content.isEmpty())
            {
                return NO_SHADER;
            }
        } catch (final IOException e)
        {
            Nebula.INSTANCE.getLogger().error(e);
            return NO_SHADER;
        }

        final int shader = glCreateShader(shaderType);
        glShaderSource(shader, content);
        glCompileShader(shader);

        if (glGetShaderi(shader, GL_COMPILE_STATUS) == GL_FALSE)
        {
            throw new RuntimeException("Failed to compile shader with type " + shaderType + " - "
                    + glGetProgramInfoLog(shader, glGetShaderi(shader, GL_INFO_LOG_LENGTH)));
        }
        return shader;
    }
}

