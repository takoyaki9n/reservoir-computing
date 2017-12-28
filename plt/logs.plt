set terminal png
set xlabel "input range(nM/min)"
set ylabel "error(NRMSE)"
#set xrange [0:1.1]
set yrange [0:2]

set style fill solid 1.00 border 0
set style histogram errorbars gap 2 lw 1
set style data histogram

set bars fullwidth
set style fill pattern 1 border lt -1
set style histogram errorbars gap 2 lw 1

dat = dir."/logs.dat"

img = dir."logsA.png"
set output img
plot dat i 0 using 2:3:xticlabels(1) ti "tau=10(min)",\
    dat i 0 using 4:5:xticlabels(1) ti "tau=25(min)",\
    dat i 0 using 6:7:xticlabels(1) ti "tau=50(min)",\
    dat i 0 using 8:9:xticlabels(1) ti "tau=100(min)"

img = dir."logsB.png"
set output img
plot dat i 1 using 2:3:xticlabels(1) ti "tau=10(min)",\
    dat i 1 using 4:5:xticlabels(1) ti "tau=25(min)",\
    dat i 1 using 6:7:xticlabels(1) ti "tau=50(min)",\
    dat i 1 using 8:9:xticlabels(1) ti "tau=100(min)"
