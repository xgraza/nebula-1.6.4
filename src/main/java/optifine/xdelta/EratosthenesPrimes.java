package optifine.xdelta;

public class EratosthenesPrimes
{
    static BitArray sieve;
    static int lastInit = -1;

    public static synchronized void reset()
    {
        sieve = null;
        lastInit = -1;
    }

    public static synchronized void init(int maxNumber)
    {
        if (maxNumber > lastInit)
        {
            int sqrt = (int)Math.ceil(Math.sqrt((double)maxNumber));
            lastInit = maxNumber;
            maxNumber >>= 1;
            ++maxNumber;
            sqrt >>= 1;
            ++sqrt;
            sieve = new BitArray(maxNumber + 1);
            sieve.set(0, true);

            for (int i = 1; i <= sqrt; ++i)
            {
                if (!sieve.get(i))
                {
                    int currentPrime = (i << 1) + 1;

                    for (int j = i * ((i << 1) + 2); j <= maxNumber; j += currentPrime)
                    {
                        sieve.set(j, true);
                    }
                }
            }
        }
    }

    public static synchronized int[] getPrimes(int maxNumber)
    {
        int primesNo = primesCount(maxNumber);

        if (primesNo <= 0)
        {
            return new int[0];
        }
        else if (maxNumber == 2)
        {
            return new int[] {2};
        }
        else
        {
            init(maxNumber);
            int[] primes = new int[primesNo];
            int maxNumber_2 = maxNumber - 1 >> 1;
            byte prime = 0;
            int var6 = prime + 1;
            primes[prime] = 2;

            for (int i = 1; i <= maxNumber_2; ++i)
            {
                if (!sieve.get(i))
                {
                    primes[var6++] = (i << 1) + 1;
                }
            }

            return primes;
        }
    }

    public static synchronized int primesCount(int number)
    {
        if (number < 2)
        {
            return 0;
        }
        else
        {
            init(number);
            int maxNumber_2 = number - 1 >> 1;
            int primesNo = 1;

            for (int i = 1; i <= maxNumber_2; ++i)
            {
                if (!sieve.get(i))
                {
                    ++primesNo;
                }
            }

            return primesNo;
        }
    }

    public static synchronized int belowOrEqual(int number)
    {
        if (number < 2)
        {
            return -1;
        }
        else if (number == 2)
        {
            return 2;
        }
        else
        {
            init(number);
            int maxNumber_2 = number - 1 >> 1;

            for (int i = maxNumber_2; i > 0; --i)
            {
                if (!sieve.get(i))
                {
                    return (i << 1) + 1;
                }
            }

            return -1;
        }
    }

    public static int below(int number)
    {
        return belowOrEqual(number - 1);
    }
}
