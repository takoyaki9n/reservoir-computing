set title "tau = ".tau." [min], S = ".S." [nM/min]"
set xlabel "time [min]"
# set ylabel "concentration [nM]"

set xrange [1000:3000]
# set yrange [0:20]

set terminal png

img = dir."/resultA.png"
set output img
task = dir."/task_TaskA.dat"
res = dir."/result_TaskA.dat"
plot res w l ti "output", \
    task w l ti "target"

img = dir."/resultB.png"
set output img
task = dir."/task_TaskB10.dat"
res = dir."/result_TaskB10.dat"
plot res w l ti "output", \
    task w l ti "target"
