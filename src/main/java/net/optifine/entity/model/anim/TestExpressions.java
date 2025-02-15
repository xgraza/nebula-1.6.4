package net.optifine.entity.model.anim;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class TestExpressions
{
    public static void main(String[] args) throws Exception
    {
        ExpressionParser ep = new ExpressionParser((IExpressionResolver)null);

        while (true)
        {
            try
            {
                while (true)
                {
                    InputStreamReader e = new InputStreamReader(System.in);
                    BufferedReader br = new BufferedReader(e);
                    String line = br.readLine();

                    if (line.length() <= 0)
                    {
                        return;
                    }

                    IExpression expr = ep.parse(line);

                    if (expr instanceof IExpressionFloat)
                    {
                        IExpressionFloat eb = (IExpressionFloat)expr;
                        float val = eb.eval();
                        System.out.println("" + val);
                    }

                    if (expr instanceof IExpressionBool)
                    {
                        IExpressionBool eb1 = (IExpressionBool)expr;
                        boolean val1 = eb1.eval();
                        System.out.println("" + val1);
                    }
                }
            }
            catch (Exception var8)
            {
                var8.printStackTrace();
            }
        }
    }
}
