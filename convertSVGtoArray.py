name = 'sword'
with open(f'{name}.svg','r') as f:
    line = f.readline().strip()
    c = 1
    while line.strip()!='':
#        print(c,line)
        c+=1
        line = f.readline().strip()
        
        if line[:8] == "<polygon":
            line = line.split()[2:]
            line[0] = line[0][8:]
            line[-1] = line[-1][:-3]

            points = []
            point = []
            for coord in line:
                if len(point)<2:
                    point.append(float(coord))
                else:
                    points.append(point)
                    point=[float(coord)]
            points.append(point)
            break
        
        if line[:9] == "<polyline":
            line = line.split()[2:]
#            print(line)
            line[0] = line[0][8:]
            line[-1] = line[-1][:-3]

            points = []
            point = []
            for coord in line:
                if len(point)<2:
                    point.append(float(coord))
                else:
                    points.append(point)
                    point=[float(coord)]
            points.append(point)
            break

min_x = min(p[0] for p in points)
max_x = max(p[0] for p in points)
min_y = min(p[1] for p in points)
max_y = max(p[1] for p in points)

center_x = (max_x-min_x)/2
center_y = (max_y-min_y)/2

points = [[x-center_x,y-center_y] for [x,y] in points]

sf = 10/max(max_y-center_y,max_x-center_x)

points = [[x*sf,y*sf] for [x,y] in points]

'''
#Create Interpolation Points
iCount = 1
iPoints = []
for i in range(len(points)-1):
    dx = (points[i+1][0]-points[i][0])/(1+iCount)
    dy = (points[i+1][1]-points[i][1])/(1+iCount)
    for j in range(iCount):
        iPoints.append([points[i][0]+(j+1)*dx,points[i][1]+(j+1)*dy])
'''


print("private static Vector2d[] " + name + " = new Vector2d[] {")

for i in range(len(points)):
    point = points[i]
    if i==len(points)-1:
        print(f"new Vector2d({point[0]},{point[1]})")
    else:
        print(f"new Vector2d({point[0]},{point[1]}),") 
#         for j in range(iCount):
#             print(f"new Vector2d({iPoints[i*iCount+j][0]},{iPoints[i*iCount+j][1]}),")

print("};")
print('is even :',(len(points))%2==0)
