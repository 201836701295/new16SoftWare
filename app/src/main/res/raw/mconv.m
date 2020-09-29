function [c] =
mconv(a, b, n
)
Fa = fft(a, n);
Fb = fft(b, n);
Fc = Fa.*Fb;
c = real(ifft(Fc, n));
end