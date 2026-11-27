# Order & Shipping Manager (Logistics Hub)

**Group 1 · BSIS 2nd Year — Data Structures & Algorithms (Final Project)**  
Built with **Java 17 LTS** and **Java Swing**. Zero external libraries and zero `java.util.*` collections.

Logistics Hub is an offline desktop program for the back room of an online store (like a Shopee or Lazada warehouse). You double-click one executable JAR file and it opens right away—no database setup, no IDE required. Because everything stays in memory, every launch starts from the same clean sample data.

---

## How the Four Screens Map to Our DSA Modules

Each screen in the left sidebar handles one daily warehouse job and demonstrates one required module from [`FINAL-Project-Guide (1).pdf`](docs/guidelines/FINAL-Project-Guide%20(1).pdf):

| Tab (Shortcut) | Daily Warehouse Job | Underlying Data Structure / Algorithm |
| :--- | :--- | :--- |
| **Orders** (`Ctrl+1`) | Queue incoming customer orders, ship the front order with weight check, or cancel without losing stock accuracy | **Circular Array Queue (`OrderQueue`)** — FIFO wrap-around carousel (capacity 20), *O(1)* enqueue/dequeue |
| **Products** (`Ctrl+2`) | View catalog sorted by product code, search by code or name, add new items, or discontinue old ones | **Binary Search Tree (`InventoryBST` + `SKUNode`)** — recursive insert, iterative search, in-order traversal, 3-case delete |
| **Restock** (`Ctrl+3`) | Log incoming supplier deliveries and safely undo the newest restock mistake | **LIFO Array Stack (`RestockStack`)** — *O(1)* push/pop + anti-lost-update subtraction undo (`BR-06`) |
| **Shipments** (`Ctrl+4`) | Re-order dispatched parcels by parcel weight, delivery speed, package number, or destination hub | **Dynamic Array (`FreightPackageArray`) + Custom Sorters** — Hoare `QuickSort` (weight/ID) & stable `InsertionSort` (speed/hub) |

Checking **Technical details** at the bottom-left of the sidebar reveals internal metrics live on screen: BST height and node count, arrival sequence numbers (`Seq #`), and the active sorting algorithm (`QuickSort (Hoare)` or `InsertionSort (Stable)`).

---

## Documentation & Submission Guides

- **Full Technical Documentation & User Guide (PDF):** [`docs/LogisticsHub-Documentation.pdf`](docs/LogisticsHub-Documentation.pdf)
- **Markdown Documentation:** [`docs/TECHNICAL_DOCUMENTATION.md`](docs/TECHNICAL_DOCUMENTATION.md)
- **Course Project Guides:** [`docs/guidelines/`](docs/guidelines/)

---

## Project Structure

```text
DSAjava/
├── src/com/group1/logistics/
│   ├── Main.java                 Entry point; applies system Look & Feel and launches MainFrame on EDT
│   ├── models/                   Product, Order, RestockAction, FreightPackage (bounds & stock guards)
│   ├── datastructures/           OrderQueue, RestockStack, InventoryBST, SKUNode, FreightPackageArray
│   ├── sorting/                  SortCriteria, PackageCriteria, QuickSort, InsertionSort
│   ├── service/                  FulfillmentService (BR-01..BR-09, live queued reservation), OpResult
│   └── ui/                       MainFrame, SidebarPanel, UiTheme, 4 module panels, 4 table models, 3 modals
├── test/com/group1/logistics/    6 standalone Java test & benchmark suites (no JUnit needed)
├── docs/                         Technical documentation (Markdown + PDF) and course guidelines
├── LogisticsHub.jar              Standalone executable JAR (Java 17)
└── run.bat                       Windows 1-click launcher
```

### Architecture Rules
- **Strict Layering:** UI screens and `AbstractTableModel` classes never mutate arrays, stacks, queues, or BST pointers directly. Every button and form calls `FulfillmentService` and receives an `OpResult` back.
- **Zero Collections:** `ArrayList`, `LinkedList`, `Queue`, `Stack`, `HashMap`, `Arrays.sort`, `Comparator`, `java.time.*`, and `java.text.*` are never used anywhere in `src/`.
- **Custom Swing Tables:** `DefaultTableModel` and `TableRowSorter` are never used. Table cells are strictly non-editable, and column header clicks invoke custom sorters via `MouseAdapter`.

---

## Business Rules (`BR-01` to `BR-09`)

1. **`BR-01` Unique Product Codes:** Positive integer SKU IDs only; duplicates are rejected (`left < root < right`).
2. **`BR-02` Valid Orders:** Orders require an existing SKU, quantity `>= 1`, non-empty customer name and hub (no `|`), and priority `1..5` (the modal offers **Express**, **Standard**, and **Economy** across **Manila Central Hub**, **Quezon City Hub**, and **Makati Logistics Hub**).
3. **`BR-03` Peek -> Validate -> Commit:** Shipping checks SKU existence, physical stock, and parcel weight before removing the front order from `OrderQueue`.
4. **`BR-04` Non-Negative Stock:** Physical `stockQty` can never drop below zero.
5. **`BR-05` Atomic Restock:** Validates SKU existence and `!restockStack.isFull()` before modifying stock or stack.
6. **`BR-06` Safe Undo (Anti-Lost-Update):** Subtracts `quantityAdded` from current physical stock rather than restoring an old snapshot, blocking rollback if those units already shipped.
7. **`BR-07` Safe Deletion:** Product deletion requires confirmation and handles Leaf, 1-Child, and 2-Child (In-Order Successor) cases with a single-decrement guard.
8. **`BR-08` Valid Shipments:** Created only on valid dispatch (`0.01–1000.00 kg` service ceiling; `0.01–50.00 kg` in the dispatch prompt).
9. **`BR-09` Isolated Sorting:** Sorting re-orders `FreightPackageArray` only and never touches `InventoryBST`, `OrderQueue`, or `RestockStack`.

---

## Running, Testing & Packaging (Java 17)

### Run Immediately
Double-click `LogisticsHub.jar` or `run.bat`, or run in PowerShell / Command Prompt:
```powershell
java -jar LogisticsHub.jar
```

### Compile & Run All 6 Test Suites
```powershell
javac --release 17 -encoding UTF-8 -d bin (Get-ChildItem -Recurse -Filter *.java src).FullName
javac --release 17 -encoding UTF-8 -cp bin -d bin-test (Get-ChildItem -Recurse -Filter *.java test).FullName

java -cp "bin;bin-test" com.group1.logistics.LinearADTTest
java -cp "bin;bin-test" com.group1.logistics.BSTTest
java -cp "bin;bin-test" com.group1.logistics.SortTest
java -cp "bin;bin-test" com.group1.logistics.ServiceTest
java -cp "bin;bin-test" com.group1.logistics.FullSystemCheckupTest
java -cp "bin;bin-test" com.group1.logistics.UltimatePerformanceTest
```

### Compliance Scan (Verify Zero Prohibited Imports)
```powershell
Select-String -Path (Get-ChildItem -Recurse -Filter *.java src).FullName `
  -Pattern 'import\s+java\.(util|time|text)\.|new\s+DefaultTableModel|TableRowSorter|setAutoCreateRowSorter\s*\(\s*true\s*\)'
```
