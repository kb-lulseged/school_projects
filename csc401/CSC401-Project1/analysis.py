import pandas as pd
import matplotlib.pyplot as plt
import numpy as np

# Read the data from CSV file
data = pd.read_csv('sorting_results.csv')

# Extract data
sizes = data['ArraySize'].values
comparisons = data['Comparisons'].values
times = data['TimeMillis'].values

# Create scatter plots
fig, (ax1, ax2) = plt.subplots(1, 2, figsize=(15, 6))

# Plot 1: Key Comparisons vs Array Size
ax1.scatter(sizes, comparisons, color='blue', alpha=0.7, s=50)
ax1.set_xlabel('Array Size (n)')
ax1.set_ylabel('Number of Key Comparisons')
ax1.set_title('Key Comparisons vs Array Size\n(Insertion Sort)')
ax1.grid(True, alpha=0.3)

# quadratic fit using numpy polyfit
coeffs_comp = np.polyfit(sizes, comparisons, 2)  # degree 2 polynomial
poly_comp = np.poly1d(coeffs_comp)
sizes_smooth = np.linspace(sizes.min(), sizes.max(), 100)
ax1.plot(sizes_smooth, poly_comp(sizes_smooth), 'r-', linewidth=2, label='Quadratic Fit')
ax1.legend()

# Plot 2: Running Time vs Array Size
ax2.scatter(sizes, times, color='red', alpha=0.7, s=50)
ax2.set_xlabel('Array Size (n)')
ax2.set_ylabel('Running Time (milliseconds)')
ax2.set_title('Running Time vs Array Size\n(Insertion Sort)')
ax2.grid(True, alpha=0.3)

# quadratic fit for time
coeffs_time = np.polyfit(sizes, times, 2)  # degree 2 polynomial
poly_time = np.poly1d(coeffs_time)
ax2.plot(sizes_smooth, poly_time(sizes_smooth), 'g-', linewidth=2, label='Quadratic Fit')
ax2.legend()

plt.tight_layout()
plt.savefig('insertion_sort_analysis.png', dpi=300, bbox_inches='tight')
plt.show()

# Results SUMMARY
print("=== DATA SUMMARY ===")
print(f"Data points analyzed: {len(sizes)}")
print(f"Array size range: {sizes.min()} - {sizes.max()}")
print(f"Minimum comparisons: {comparisons.min():,}")
print(f"Maximum comparisons: {comparisons.max():,}")
print()

# Estimate for n=10,000
predicted_comparisons_10k = poly_comp(10000)
predicted_time_10k = poly_time(10000)

print(f"Predicted comparisons for n=10,000: {predicted_comparisons_10k:,.0f}")
print(f"Predicted running time for n=10,000: {predicted_time_10k:.1f} ms")
print()

print("=== DETAILED RESULTS TABLE ===")
print("Size     | Comparisons | Time (ms) | Comp/n²    | Theoretical")
print("-" * 65)
for i in range(len(sizes)):
    comp_per_n2 = comparisons[i] / (sizes[i] ** 2)
    theoretical = (sizes[i] * (sizes[i] - 1)) // 4  # Approximate average case
    print(f"{sizes[i]:5d} | {comparisons[i]:11,} | {times[i]:8.1f} | {comp_per_n2:.6f} | {theoretical:11,}")

print()
