set terminal png
img = case."/waves_".mode.".png"
set output img
waves = case."/waves_".mode.".txt"
plot waves u 1 w l, waves u 2 w l, waves u 3 w l, waves u 4 w l, waves u 5 w l, waves u 6 w l