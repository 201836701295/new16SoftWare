function [dba] =
mslm(x)
Fs = 44100;
X = fft(x);
X = abs(X);
X(X
== 0) = 1e-17;
temp = 0
:(
length(X)
-1);
f = (Fs / length(X)).*temp;
ind = find(f < Fs / 2);
f = f(ind);
X = X(ind);
f(f
== 0) = 1e-17;
%
filterA
        c1 = 3.5041384e16;
c2 = 20.598997 ^2;
c3 = 107.65265 ^2;
c4 = 737.86223 ^2;
c5 = 12194.217 ^2;
f = f.^2;
num = c1 * f.^4;
den = ((c2 + f).^ 2).*(c3 + f).*(c4 + f).*((c5 + f).^ 2);
A = num./ den;

X = A
'.*X;
totalEnergy = sum(X.^ 2) / length(X);
meanEnergy = totalEnergy / ((1 / Fs) * length(x));
dba = 10 * log10(meanEnergy);
end