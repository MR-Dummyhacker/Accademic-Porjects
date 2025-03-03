import numpy as np

Graph = np.array([[0, 1, 5],
                  [0, 3, 6],
                  [0, 10, 31],
                  [0, 12, 18],
                  [0, 8, 15],
                  [0, 7, 21],
                  [1, 3, 3],
                  [1, 2, 1],
                  [1, 7, 26],
                  [1, 5, 29],
                  [2, 3, 4],
                  [2, 4, 6],
                  [2, 6, 22],
                  [2, 5, 28],
                  [3, 4, 2],
                  [3, 11, 30],
                  [3, 14, 24],
                  [3, 10, 30],
                  [4, 6, 19],
                  [4, 9, 16],
                  [4, 13, 20],
                  [4, 11, 32],
                  [5, 6, 7],
                  [5, 7, 11],
                  [6, 9, 10],
                  [7, 8, 9],
                  [8, 12, 13],
                  [9, 13, 14],
                  [10, 11, 17],
                  [10, 14, 23],
                  [10, 12, 28],
                  [11, 13, 25],
                  [11, 14, 27],
                  [12, 14, 8],
                  [13, 14, 12]])

Vertices = len(Graph)


def find(parent, i):
    if parent[i] != i:
        parent[i] = find(parent, parent[i])
    return parent[i]


def union(parent, rank, x, y):
    if rank[x] < rank[y]:
        parent[x] = y
    elif rank[x] > rank[y]:
        parent[y] = x
    else:
        parent[y] = x
        rank[x] += 1


results = []

i = 0

e = 0

Graph = sorted(Graph, key=lambda item: item[2])
parent = []
rank = []

print(Graph)

for node in range(Vertices):
    parent.append(node)
    rank.append(0)

print(parent)
print(rank)

for e in range(Vertices):
    u, v, w = Graph[e]
    #i = i + 1
    x = find(parent, u)
    y = find(parent, v)

    if x != y:
        #e = e + 1
        results.append([u, v, w])
        union(parent, rank, x, y)
        print("parent:  " , parent)
        print("rank:  " , rank)
        print(results)

minimumCost = 0
print("Edges in the MST")
for u, v, w in results:
    minimumCost += w
    print(u, "--", v, "=", w)
print("Minimum Spanning Tree", minimumCost)
