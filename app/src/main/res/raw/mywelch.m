function [pxx,f] =
mywelch(x, N, fs
)
[pxx,f] =
pwelch(x,
[],[],N,fs);
pxx = 10 * log10(pxx);
end