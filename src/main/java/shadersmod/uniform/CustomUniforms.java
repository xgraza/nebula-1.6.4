package shadersmod.uniform;

public class CustomUniforms
{
    private CustomUniform[] uniforms;

    public CustomUniforms(CustomUniform[] uniforms)
    {
        this.uniforms = uniforms;
    }

    public void setProgram(int program)
    {
        for (int i = 0; i < this.uniforms.length; ++i)
        {
            CustomUniform uniform = this.uniforms[i];
            uniform.setProgram(program);
        }
    }

    public void update()
    {
        for (int i = 0; i < this.uniforms.length; ++i)
        {
            CustomUniform uniform = this.uniforms[i];
            uniform.update();
        }
    }
}
