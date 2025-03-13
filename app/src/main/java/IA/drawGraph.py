import os
import re
import networkx as nx
import matplotlib.pyplot as plt

# Expresión regular mejorada para detectar conexiones sensor-sensor y sensor-center
edge_pattern = re.compile(
    r"Edge: distance: (\d+\.\d+) (?:dstReception|centerReception): (\d+) cost: (\d+)\s+"
    r"sensor\(x=(\d+), y=(\d+), capacity=(\d+)\) -> "
    r"(?:sensor\(x=(\d+), y=(\d+), capacity=\d+\)|center\(x=(\d+), y=(\d+)\))"
)

# Función para leer el archivo y construir el grafo
def build_graph_from_file(file_path):
    G = nx.DiGraph()  # Grafo dirigido
    total_cost = 0
    total_volume = 0

    try:
        with open(file_path, "r") as file:
            for line in file:
                match = edge_pattern.search(line)
                if match:
                    distance, reception, cost, sx, sy, capacity, sx2, sy2, cx, cy = match.groups()
                    
                    # Identificar nodos
                    sensor_node = f"S({sx},{sy})"
                    sensor_capacity = int(capacity)  # Obtener la capacidad del sensor
                    if cx and cy:  # Conexión a un centro
                        target_node = f"C({cx},{cy})"
                        G.add_node(target_node, pos=(int(cx), int(cy)), type="center")
                    else:  # Conexión a otro sensor
                        target_node = f"S({sx2},{sy2})"
                        G.add_node(target_node, pos=(int(sx2), int(sy2)), type="sensor")

                    # Agregar nodos y arista
                    G.add_node(sensor_node, pos=(int(sx), int(sy)), type="sensor", capacity=sensor_capacity)
                    G.add_edge(sensor_node, target_node, distance=float(distance), cost=int(cost), reception=int(reception))

                    # Sumar costos y volumen
                    total_cost += int(cost)
                    total_volume += int(capacity)

        return G, total_cost, total_volume
    except FileNotFoundError:
        print(f"Error: El archivo {file_path} no existe.")
        return None, 0, 0

# Función para dibujar el grafo con colores diferenciados
def draw_graph(G, total_cost, total_volume, output_path):
    if G is None or len(G.nodes) == 0:
        print("No hay datos para dibujar el grafo.")
        return
    
    num_nodes = len(G.nodes)
    figsize = (max(10, num_nodes / 10), max(6, num_nodes / 10))
    plt.figure(figsize=figsize)
    
    pos = nx.get_node_attributes(G, "pos")  
    labels = {node: f"{node}\nCap:{G.nodes[node].get('capacity', 'N/A')}" for node in G.nodes}

    # Definir colores: sensores azules, centros rojos
    node_colors = ["red" if G.nodes[n]["type"] == "center" else "blue" for n in G.nodes]

    # Dibujar el grafo con nodos transparentes
    nx.draw(G, pos, with_labels=True, labels=labels, node_size=1000, node_color=node_colors, font_size=6, font_color="white", edge_color="gray", alpha=0.5)

    # Etiquetas para las aristas (costo + distancia + recepción)
    edge_labels = {(u, v): f"C:{d['cost']}, D:{d['distance']:.2f}, R:{d['reception']}" for u, v, d in G.edges(data=True)}
    nx.draw_networkx_edge_labels(G, pos, edge_labels=edge_labels, font_size=6)

    plt.title("Grafo de Sensores y Centros de Recepción")

    # Mostrar valores de coste y volumen
    plt.figtext(0.01, 0.01, f"Coste Total: {total_cost}", fontsize=12, ha="left")
    plt.figtext(0.01, 0.05, f"Volumen Total: {total_volume} Mbits", fontsize=12, ha="left")

    # Guardar imagen
    plt.savefig(output_path, dpi=300)
    plt.close()
    print(f"Grafo guardado en '{output_path}'")

# Directorios de entrada y salida
in_file_path_dir = "./app/src/main/java/IA/outputs/"
out_file_path_dir = "./app/src/main/java/IA/graphs/"

# Expresión regular para capturar el número del archivo outputX.out
file_pattern = re.compile(r"output(\d+)\.out")

# Obtener y ordenar los archivos por su índice numérico
input_files = sorted(
    (f for f in os.listdir(in_file_path_dir) if file_pattern.match(f)),
    key=lambda x: int(file_pattern.match(x).group(1))  # Extraer y ordenar por el índice numérico
)

print("Archivos detectados:", input_files)

# Procesar cada archivo de entrada
for input_file in input_files:
    match = file_pattern.match(input_file)
    if match:
        index = match.group(1)
        file_path = os.path.join(in_file_path_dir, input_file)
        output_path = os.path.join(out_file_path_dir, f"graph{index}.png")

        # Construir el grafo y generar la imagen
        graph, total_cost, total_volume = build_graph_from_file(file_path)
        draw_graph(graph, total_cost, total_volume, output_path)
