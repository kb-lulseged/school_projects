import numpy as np 
import matplotlib.pyplot as plt

# LOADING AND PREPROCESSING

def load_data(filepath):
    data = np.loadtxt(filepath)

    X = data[:, 0:4]  # Inputs
    y = data[:, 4:5]  # Outputs

    return X, y


# normalizing data using min-max normalization
def normalize_data(X, y):
    X_min = X.min(axis=0)
    X_max = X.max(axis=0)

    X_norm = (X - X_min) / (X_max - X_min + 1e-8)

    y_min = y.min();
    y_max = y.max();
    y_norm = (y - y_min) / (y_max - y_min + 1e-8)

    norm_params = {
            'X_min': X_min, 'X_max': X_max,
            'y_min': y_min, 'y_max': y_max
            }

    return X_norm, y_norm, norm_params


def train_test_split(X, y, train_ratio=0.5, seed=42):
    np.random.seed(seed)

    num_samples = X.shape[0]
    num_train = int(num_samples * train_ratio)
    indices = np.random.permutation(num_samples)

    train_idx = indices[:num_train]
    test_idx = indices[num_train:]

    return X[train_idx], y[train_idx], X[test_idx], y[test_idx]



# ACTIVATION FUNCTIONS

def relu(z):
    return np.maximum(0, z)

def relu_derivative(z):
    return (z > 0).astype(float)


# NEURAL NETWORK

class NeuralNetwork:
    # initalize with random weights using He initialization
    def __init__(self, input_size=4, hidden_size=32, output_size=1, learning_rate=0.01):
        self.learning_rate = learning_rate
        self.W1 = np.random.randn(input_size, hidden_size) * np.sqrt(2.0 / input_size)
        self.b1 = np.zeros((1, hidden_size))
        self.W2 = np.random.randn(hidden_size, output_size) * np.sqrt(2.0 / hidden_size)
        self.b2 = np.zeros((1, output_size))

        # storing for back prop 
        self.z1 = None
        self.a1 = None
        self.z2 = None

    def forward(self, X):
        # hidden layer 
        self.z1 = X @ self.W1 + self.b1 
        self.a1 = relu(self.z1)

        # output layer
        self.z2 = self.a1 @ self.W2 + self.b2 

        return self.z2 

    def compute_loss(self, y_pred, y_true):
        n = y_true.shape[0]
        mse = np.sum((y_pred - y_true) ** 2) / n 
        return mse

    def backward(self, X, y_true, y_pred):
        n = X.shape[0]
        
        # output layer gradients
        dz2 = (2/n) * (y_pred - y_true)
        dW2 = self.a1.T & dz2 
        db2 = np.sum(dz2, axis=0, keepdims=True)

        # hidden layer gradients 
        da1 = dz2 @ self.W2.T 
        dW1 = X.T @ dz1 
        db1 = np.sum(dz1, axis=0, keepdims=True)

        return dW1, db1, dW2, db2 

    def update_weights(self, dW1, db1, dW2, db2):
        self.W1 -= self.learning_rate * dW1
        self.b1 -= self.learning_rate * db1 
        self.W2 -= self.learning_rate * dW2 
        self.b2 -= self.learning_rate * db2 

    def train_step(self, X, y):
        y_pred = self.forward(X)
        loss = self.compute_loss(y_pred, y)
        dW1, db1, dW2, db2 = self.backward(X, y, y_pred)
        self.update_weights(dW1, db1, dW2, db2)

        return loss 


# Train model using SGD and early stopping
def train(model, X_train, y_train, X_test, y_test, epochs=100, batch_size=32):
    train_losses = []
    test_losses = []

    n_train = X_train.shape[0]

    best_test_loss = float('inf')
    patience = 10  # how many epochs of no improvement to tolerate
    patience_counter = 0 
    best_weights = None

    for epoch in range(epochs):
        shuffle_idx = np.random.permutation(n_train)
        X_shuffled = X_train[shuffle_idx]
        y_shuffled = y_train[shuffle_idx]

        epoch_losses = []

        for i in range(0, n_train, batch_size):
            X_batch = X_shuffled[i:i+batch_size]
            y_batch = y_shuffled[i:i+batch_size]

            batch_loss = model.train_step(X_batch, y_batch)
            epoch_losses.append(batch_loss)

        train_loss = np.mean(epoch_losses)
        train_losses.append(train_loss)

        # Evaluate on test set 
        y_test_pred = model.forward(X_test)
        test_loss = model.compute_loss(y_test_pred, y_test)
        test_losses.append(test_loss)

        # early stop check 
        if test_loss < best_test_loss:
            best_test_loss = test_loss
            patience_counter = 0 
            best_weights = {
                    'W1': model.W1.copy(),
                    'b1': model.b1.copy(),
                    'W2': model.W2.copy(),
                    'b2': model.b2.copy()
                    }
        else:
            patience_counter += 1 

        if (epoch + 1) % 10 == 0:
            printf(f"Epoc {epoch+1}/{epochs} | Train loss: {train_loss:.6f} | Test Loss: {test_loss:.6f}")

        if patience_counter >= patience:
            print(f"\nEarly stopping at epoch {epoch + 1}, no improvement for {patience} epochs")
            # change weights back 
            model.W1 = best_weights['W1']
            model.b1 = best_weights['b1']
            model.W2 = best_weights['W2']
            model.b2 = best_weights['b2']
            break

    return train_losses, test_losses 



    
# PLOTTING GRAPHS
def plot_losses(train_losses, test_losses, save_path='error_plot.png'):
    plt.figure(figsize=(10,6))
    epochs = range(1, len(train_losses) + 1)

    plt.plot(epochs, train_losses, 'b-', label='Training Error', linewidth=2)
    plt.plot(epochs, test_losses, 'r-', label='Test Error', linewidth=2)

    plt.xlabel('Epoch', fontsize=12)
    plt.ylabel('Mean Squared Error', fontsize=12)
    plt.title('Training and Test Error over Time', fontsize=14)
    plt.legend(fontsize=11)
    plt.grid(True, alpha=0.3)

    if max(train_losses) / min(train_losses) > 100:
        plt.yscale('log')
        plt.ylabel('Mean Squared Error (log scale)', fontsize=12)

    plt.tight_layout()
    plt.savefig(save_path, dpi=150)
    plt.show()
    print(f"Plot saved to {save_path}")



# STORING WEIGHTS 
def save_weights(model, filepath='final_weights.txt'):
    with open(filepath, 'w') as f:
        f.write("=" * 60 + "\n")
        f.write("BRDF Neural Network - Trained Weights\n")
        f.write("=" * 60 + "\n\n")

        f.write("LAYER 1 (input --> hidden) \n")
        f.write("-" * 40 + "\n")
        f.write(f"W1 shape: {model.W1.shape}\n")
        f.write("W1 values: \n")
        np.savetxt(f, model.W1, fmt='%.8f')
        f.write(f"\nb1 shape: {model.b1.shape}\n")
        f.write("b1 values:\n")
        np.savetxt(f, model.b1, fmt='%.8f')

        f.write("\n\nLAYER 2 (hidden --> output)\n")
        f.write("-" * 40 + "\n")
        f.write(f"W2 shape: {model.W2.shape}\n")
        f.write("W2 values:\n")
        np.savetxt(f, model.W2, fmt='%.8f')
        f.write(f"\nb2 shape: {model.b2.shape}\n")
        f.write("b2 values:\n")
        np.savetxt(f, model.b2, fmt='%.8f')

    print(f"Weights saved to {filepath}")




def main():
    DATA_FILE = 'black_obsidian_data.txt'
    HIDDEN_SIZE = 64
    LEARNING_RATE = 0.01
    EPOCHS = 200
    BATCH_SIZE = 64

    print("=" * 60)
    print("BRDF Neural Network Training")
    print("=" * 60)

    # Load Data 
    print("\n[1/7] Loading data...")
    X, y = load_data(DATA_FILE)
    print(f"    Loaded {X.shape[0]} samples")
    print(f"    Input shape: {X.shape}, Output shape: {y.shape}")

    # Normalize 
    print("\n[2/7] Normalizing data...")
    X_norm, y_norm, norm_params = normalize_data(X, y)
    print(f"    Input range: [{X_norm.min():.3f}, {X_norm.max():.3f}]")
    print(f"    Output range: [{y_norm.min():.3f}, {y_norm.max():.3f}]")

    # Split 
    print("\n[3/7] Splitting into train/test (50/50)...")
    X_train, y_train, X_test, y_test = train_test_split(X_norm, y_norm)
    print(f"    Training samples: {X_train.shape[0]}")
    print(f"    Test samples: {X_test.shape[0]}")

    # Create network 
    print("\n[4/7] Creating neural network...")
    model = NeuralNetwork(
            input_size=4, 
            hidden_size=HIDDEN_SIZE,
            output_size=1,
            learning_rate=LEARNING_RATE
            )
    print(f"    Architecture: 4 --> {HIDDEN_SIZE} (ReLU) --> 1 (linear)")
    print(f"    Learning rate: {LEARNING_RATE}")

    # Train 
    print("\n[5/7] Training network...")
    print("-" * 40)
    train_losses, test_losses = train(
            model, X_train, y_train, X_test, y_test, 
            epochs=EPOCHS, batch_size=BATCH_SIZE
            )
    print("-" * 40)
    print(f"    Final train loss: {train_losses[-1]:.6f}")
    print(f"    Final test loss: {test_losses[-1]:.6f}")

    # Graph 
    print("\n[6/7] Generating error plot...")
    plot_losses(train_losses, test_losses)

    print("\n[7/7] Saving final weights...")
    save_weights(model)

    print("\n" + "=" * 60)
    print("Training complete!")
    print("=" * 60)


if __name__ == "__main__":
    main()


