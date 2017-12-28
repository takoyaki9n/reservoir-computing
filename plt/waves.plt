set xrange [1000:3000]
set yrange [0:20]

set terminal png
img = dir."/waves.png"
set output img
waves = dir."/waves.dat"
plot waves u 1 w l, waves u 2 w l, waves u 3 w l, waves u 4 w l, waves u 5 w l, waves u 6 w l