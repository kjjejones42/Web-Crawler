# Web-Crawler 6/6: Complicated matters

## Description

At the final stage, implement a real web crawler!

We suggest you use the following algorithm.

Create some threads-workers which wait for new tasks in the task queue. A task is an URL. If a thread-worker gets a task, it goes to the page, saves its title, collects all links on the page, and adds this links as new tasks to the task queue.

So links and titles will be [breadth-first searched](https://en.wikipedia.org/wiki/Breadth-first_search).

Don't forget to exclude already visited pages from new tasks as they add redundant work.

To sum up, add the following components to your window:

* The number of threads-workers.
* Two checkable restrictions:
    * Maximum crawling depth: if enabled, workers won't go too deep in the Internet.
    * Time limit: if enabled, workers won't add tasks after the given time.
* A JToggleButton Run/Stop to toggle crawling process (the button should deactivate automatically if a restriction becomes valid or if all the links have been processed and there's nothing else to process).
* Metrics like elapsed time and parsed page count.

## Testing requirements

For the testing reasons, you need to set the name of each component using the method `component.setName(String name)`.

On this stage, please provide the following components:

|                 |                    |
|-----------------|--------------------|
| `JTextField`    | UrlTextField       |
| `JToggleButton` | RunButton          |
| `JTextField`    | DepthTextField     |
| `JCheckBox`     | DepthCheckBox      |
| `JLabel`        | ParsedLabel        |
| `JTextField`    | ExportUrlTextField |
| `JButton`       | ExportButton       |

## Example

Below there is the example of how your new window might look:

![ ](https://ucarecdn.com/d7c94d48-02da-4484-9085-9517d629f65b/)