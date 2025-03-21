import os
import re
import networkx as nx
import matplotlib.pyplot as plt

CENTER_CAPACITY = 150
NODE_TEXT_FONT_SIZE = 8
TOTAL_TEXT_FONT_SIZE = 20

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
    sensor_nodes = set()  # Track sensor nodes
    center_nodes = set()  # Track center nodes

    try:
        with open(file_path, "r") as file:
            for line in file:
                # Detect sensors
                sensor_match = re.search(r"sensor\(x=(\d+), y=(\d+), capacity=(\d+)\)", line)
                if sensor_match:
                    sx, sy, capacity = sensor_match.groups()
                    sensor_node = f"S({sx},{sy})"
                    sensor_nodes.add((sensor_node, int(sx), int(sy), int(capacity)))

                # Detect centers
                center_match = re.search(r"center\(x=(\d+), y=(\d+)\)", line)
                if center_match:
                    cx, cy = center_match.groups()
                    center_node = f"C({cx},{cy})"
                    center_nodes.add((center_node, int(cx), int(cy)))

                # Detect edges
                match = edge_pattern.search(line)
                if match:
                    distance, transmition, cost, sx, sy, capacity, sx2, sy2, cx, cy = match.groups()

                    if cx and cy:  # Connection to a center
                        target_node = f"C({cx},{cy})"
                    else:  # Connection to another sensor
                        target_node = f"S({sx2},{sy2})"

                    # Add edge
                    G.add_edge(sensor_node, target_node, distance=float(distance), cost=int(cost), transmition=int(transmition))

                    # Add costs and volume
                    total_cost += int(cost)
                    total_volume += int(capacity)

        # Add all sensors to the graph
        for sensor, x, y, capacity in sensor_nodes:
            G.add_node(sensor, pos=(x, y), type="sensor", capacity=capacity)

        # Add all center nodes to the graph
        for center, x, y in center_nodes:
            G.add_node(center, pos=(x, y), type="center", capacity=CENTER_CAPACITY)

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
    figsize = (max(10, num_nodes/3), max(6, num_nodes/3))
    plt.figure(figsize=figsize)
    
    pos = nx.get_node_attributes(G, "pos")  
    labels = {node: f"{node}\nCap:{G.nodes[node].get('capacity', 'N/A')}" for node in G.nodes}

    # Define colors: blue for sensors, red for centers
    node_colors = ["red" if G.nodes[n]["type"] == "center" else "blue" for n in G.nodes]

    # Draw the graph with transparent nodes
    nx.draw(G, pos, with_labels=True, labels=labels, node_size=(1000/6)*NODE_TEXT_FONT_SIZE, node_color=node_colors, font_size=NODE_TEXT_FONT_SIZE, font_color="white", edge_color="gray", alpha=0.5)

    # Edge labels (cost + distance + transmission)
    edge_labels = {(u, v): f"C:{d['cost']}, D:{d['distance']:.2f}, T:{d['transmition']}" for u, v, d in G.edges(data=True)}
    nx.draw_networkx_edge_labels(G, pos, edge_labels=edge_labels, font_size=NODE_TEXT_FONT_SIZE, bbox=dict(alpha=0))

    plt.title("Graph of Sensors and Transmission Centers")

    # Display total cost and volume values
    plt.figtext(0.01, 0.01, f"Total Cost: {total_cost}", fontsize=TOTAL_TEXT_FONT_SIZE, ha="left")
    plt.figtext(0.01, 0.03, f"Total Volume: {total_volume} Mbits", fontsize=TOTAL_TEXT_FONT_SIZE, ha="left")

    # Save image
    plt.savefig(output_path, dpi=256)
    plt.close()
    print(f"Graph saved in '{output_path}'")

# Input and output directories
in_file_path_dir = "./app/src/main/outputs/"
out_file_path_dir = "./app/src/main/graphs/"

# Regular expression to capture the output file number outputX.out
file_pattern = re.compile(r"output(?:_gen)?(\d+)\.out$")  # Matches 'output' or 'output_gen' before numbers

input_files = sorted(
    (f for f in os.listdir(in_file_path_dir) if file_pattern.match(f)),
    key=lambda x: int(file_pattern.match(x).group(1))  # Extract and sort by numeric index
)


print("Detected files:", input_files)

# Process each input file
for input_file in input_files:
    match = file_pattern.match(input_file)
    if match:
        number = match.group(1)  # Extract the numeric part

        # Check if the filename contains "_gen" and format index accordingly
        if "_gen" in input_file:
            index = f"_gen{number}"
        else:
            index = number

        file_path = os.path.join(in_file_path_dir, input_file)
        output_path = os.path.join(out_file_path_dir, f"graph{index}.jpg")

        # Build the graph and generate the image
        graph, total_cost, total_volume = build_graph_from_file(file_path)
        draw_graph(graph, total_cost, total_volume, output_path)

