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

# Simple quadratic fit using numpy polyfit
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

# Simple quadratic fit for time
coeffs_time = np.polyfit(sizes, times, 2)  # degree 2 polynomial
poly_time = np.poly1d(coeffs_time)
ax2.plot(sizes_smooth, poly_time(sizes_smooth), 'g-', linewidth=2, label='Quadratic Fit')
ax2.legend()

plt.tight_layout()
plt.savefig('insertion_sort_analysis.png', dpi=300, bbox_inches='tight')
plt.show()

# Analysis and predictions
print("=== INSERTION SORT EFFICIENCY ANALYSIS ===")
print(f"Data points analyzed: {len(sizes)}")
print(f"Array size range: {sizes.min()} - {sizes.max()}")
print()

# Estimate for n=10,000
predicted_comparisons_10k = poly_comp(10000)
predicted_time_10k = poly_time(10000)

print("=== KEY COMPARISONS ANALYSIS ===")
print(f"Minimum comparisons: {comparisons.min():,}")
print(f"Maximum comparisons: {comparisons.max():,}")
print(f"Predicted comparisons for n=10,000: {predicted_comparisons_10k:,.0f}")

# Theoretical analysis for Insertion Sort
theoretical_10k_best = 10000 - 1  # Best case: n-1 comparisons
theoretical_10k_worst = (10000 * (10000 - 1)) // 2  # Worst case: n(n-1)/2
theoretical_10k_avg = theoretical_10k_worst // 2  # Average case: ~n²/4

print()
print("Theoretical comparisons for Insertion Sort (n=10,000):")
print(f"  Best case (sorted array): {theoretical_10k_best:,}")
print(f"  Average case (random array): {theoretical_10k_avg:,}")  
print(f"  Worst case (reverse sorted): {theoretical_10k_worst:,}")
print()

print("=== RUNNING TIME ANALYSIS ===")
print(f"Predicted running time for n=10,000: {predicted_time_10k:.1f} ms")
print()

print("=== POLYNOMIAL COEFFICIENTS ===")
print("Key Comparisons fit: {:.6e}*n² + {:.6e}*n + {:.6e}".format(
    coeffs_comp[0], coeffs_comp[1], coeffs_comp[2]))
print("Running Time fit: {:.6e}*n² + {:.6e}*n + {:.6e}".format(
    coeffs_time[0], coeffs_time[1], coeffs_time[2]))
print()

print("=== HYPOTHESIS ===")
# Calculate the ratio of comparisons to n²
comp_ratios = comparisons / (sizes ** 2)
avg_ratio = np.mean(comp_ratios)
print(f"Average comparisons/n² ratio: {avg_ratio:.6f}")
print(f"Expected ratio for insertion sort average case: ~0.25 (n²/4)")

if 0.15 <= avg_ratio <= 0.35:
    print("✓ HYPOTHESIS CONFIRMED: Insertion Sort exhibits O(n²) average-case complexity")
    print("  The empirical data matches theoretical expectations (~n²/4 comparisons)")
else:
    print("? HYPOTHESIS NEEDS REVIEW: Ratio differs from expected insertion sort behavior")

print()
print("=== DETAILED RESULTS TABLE ===")
print("Size     | Comparisons | Time (ms) | Comp/n²    | Theoretical")
print("-" * 65)
for i in range(len(sizes)):
    comp_per_n2 = comparisons[i] / (sizes[i] ** 2)
    theoretical = (sizes[i] * (sizes[i] - 1)) // 4  # Approximate average case
    print(f"{sizes[i]:5d} | {comparisons[i]:11,} | {times[i]:8.1f} | {comp_per_n2:.6f} | {theoretical:11,}")

print()
print("=== SUMMARY FOR REPORT ===")
print("Algorithm: Insertion Sort")
print("Time Complexity: O(n²) average case")
print("Space Complexity: O(1)")
print("Key Comparisons: Approximately n²/4 for random input")
print(f"Prediction for n=10,000: {predicted_comparisons_10k:,.0f} comparisons")
