import os
import re
import networkx as nx
import matplotlib.pyplot as plt

# Improved regular expression to detect sensor-to-sensor and sensor-to-center connections
edge_pattern = re.compile(
    r"Edge: distance: (\d+\.\d+) (?:transmition): (\d+) cost: (\d+)\s+" 
    r"sensor\(x=(\d+), y=(\d+), capacity=(\d+)\) -> "
    r"(?:sensor\(x=(\d+), y=(\d+), capacity=\d+\)|center\(x=(\d+), y=(\d+)\))"
)

# Function to read the file and build the graph
def build_graph_from_file(file_path):
    G = nx.DiGraph()  # Directed graph
    total_cost = 0
    total_volume = 0

    try:
        with open(file_path, "r") as file:
            for line in file:
                match = edge_pattern.search(line)
                if match:
                    distance, transmition, cost, sx, sy, capacity, sx2, sy2, cx, cy = match.groups()
                    
                    # Identify nodes
                    sensor_node = f"S({sx},{sy})"
                    sensor_capacity = int(capacity)  # Get sensor capacity
                    if cx and cy:  # Connection to a center
                        target_node = f"C({cx},{cy})"
                        G.add_node(target_node, pos=(int(cx), int(cy)), type="center")
                    else:  # Connection to another sensor
                        target_node = f"S({sx2},{sy2})"
                        G.add_node(target_node, pos=(int(sx2), int(sy2)), type="sensor")

                    # Add nodes and edge
                    G.add_node(sensor_node, pos=(int(sx), int(sy)), type="sensor", capacity=sensor_capacity)
                    G.add_edge(sensor_node, target_node, distance=float(distance), cost=int(cost), transmition=int(transmition))

                    # Add costs and volume
                    total_cost += int(cost)
                    total_volume += int(capacity)

        return G, total_cost, total_volume
    except FileNotFoundError:
        print(f"Error: The file {file_path} does not exist.")
        return None, 0, 0

# Function to draw the graph with differentiated colors
def draw_graph(G, total_cost, total_volume, output_path):
    if G is None or len(G.nodes) == 0:
        print("No data to draw the graph.")
        return
    
    num_nodes = len(G.nodes)
    figsize = (max(10, num_nodes / 10), max(6, num_nodes / 10))
    plt.figure(figsize=figsize)
    
    pos = nx.get_node_attributes(G, "pos")  
    labels = {node: f"{node}\nCap:{G.nodes[node].get('capacity', 'N/A')}" for node in G.nodes}

    # Define colors: blue for sensors, red for centers
    node_colors = ["red" if G.nodes[n]["type"] == "center" else "blue" for n in G.nodes]

    # Draw the graph with transparent nodes
    nx.draw(G, pos, with_labels=True, labels=labels, node_size=1000, node_color=node_colors, font_size=6, font_color="white", edge_color="gray", alpha=0.5)

    # Edge labels (cost + distance + transmission)
    edge_labels = {(u, v): f"C:{d['cost']}, D:{d['distance']:.2f}, T:{d['transmition']}" for u, v, d in G.edges(data=True)}
    nx.draw_networkx_edge_labels(G, pos, edge_labels=edge_labels, font_size=6)

    plt.title("Graph of Sensors and Transmission Centers")

    # Display total cost and volume values
    plt.figtext(0.01, 0.01, f"Total Cost: {total_cost}", fontsize=12, ha="left")
    plt.figtext(0.01, 0.05, f"Total Volume: {total_volume} Mbits", fontsize=12, ha="left")

    # Save image
    plt.savefig(output_path, dpi=300)
    plt.close()
    print(f"Graph saved in '{output_path}'")

# Input and output directories
in_file_path_dir = "./app/src/main/java/IA/outputs/"
out_file_path_dir = "./app/src/main/java/IA/graphs/"

# Regular expression to capture the output file number outputX.out
file_pattern = re.compile(r"output(\d+)\.out")

# Get and sort files by their numeric index
input_files = sorted(
    (f for f in os.listdir(in_file_path_dir) if file_pattern.match(f)),
    key=lambda x: int(file_pattern.match(x).group(1))  # Extract and sort by numeric index
)

print("Detected files:", input_files)

# Process each input file
for input_file in input_files:
    match = file_pattern.match(input_file)
    if match:
        index = match.group(1)
        file_path = os.path.join(in_file_path_dir, input_file)
        output_path = os.path.join(out_file_path_dir, f"graph{index}.png")

        # Build the graph and generate the image
        graph, total_cost, total_volume = build_graph_from_file(file_path)
        draw_graph(graph, total_cost, total_volume, output_path)
