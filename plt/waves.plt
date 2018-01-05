set title "tau = ".tau." [min], S = ".S." [nM/min]"
set xlabel "time [min]"
set ylabel "concentration [nM]"

set xrange [1000:3000]
set yrange [0:20]

set terminal png
img = dir."/waves.png"
set output img
waves = dir."/waves.dat"
plot waves u 1 w l ti "S1",\
     waves u 3 w l ti "S2", \
     waves u 5 w l ti "S3", \
     waves u 6 w l ti "I4", \
     waves u 4 w l ti "I5", \
     waves u 2 w l ti "I6"
