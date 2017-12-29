set title "tau = ".tau." [min], S = ".S." [nM/min]"
set xlabel "time [min]"
set ylabel "input [nM/min]"

set xrange [1000:3000]
# set yrange [0:20]

set terminal png

img = dir."/input.png"
set output img
dat = dir."/input_random1.dat"
plot dat w l ti "input"
