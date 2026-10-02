# Week 3 Queue Flowchart

```text
                 START
                   |
              Initialize Queue
                   |
             Is Queue FULL?
              /          \
            YES           NO
             |             |
      Show FULL status   Enqueue item
             |             |
             +-------> Update rear/count
                             |
                        Queue status
                             |
                       Is Queue EMPTY?
                        /          \
                      YES           NO
                       |             |
                Show EMPTY status  Dequeue item
                                     |
                              Update front/count
                                     |
                               Queue status
                                     |
                                   END
```

## Required conditions covered
- Enqueue
- Dequeue
- Empty condition
- Full condition
- Queue status/update

The implementation uses a circular FIFO queue so that freed positions can be reused.
