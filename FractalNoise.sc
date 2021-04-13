FractalNoise {

   *ar { arg beta = 0, mul = 1, add = 0;
      var n = 6, h = 2, fp = 50, t = SampleDur.ir, norm = exp(beta).reciprocal**1.72, sig = WhiteNoise.ar(mul,add);

      n do: {
         var fo = 10**(0.5*beta*h.reciprocal)*fp;
         sig = FOS.ar(sig,1,exp(-2pi*fo*t).neg,exp(-2pi*fp*t));
         fp = 10**h.reciprocal*fp
      };

      ^(norm*sig)
   }

}