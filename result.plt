set terminal png
img = "case".case."/result".task."_".mode.".png"
set output img
tasks = "case".case."/tasks_".mode.".txt"
result = "case".case."/result_".mode.".txt"
plot tasks u task w l, result u task w l