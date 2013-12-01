#!/bin/bash
# Sets up a tmux session with a headless repl,
# cljsbuild auto runnning and a karma instance
# running as well.

SESSION=chaperone

tmux new-session -d -s $SESSION
tmux send-keys "vagrant up" C-m
tmux send-keys "karma start" C-m
tmux split-window -h
tmux select-pane -t 1
tmux send-keys "lein repl :headless" C-m
tmux split-window -v -p 80
tmux send-keys "lein with-profile cljs cljsbuild auto" C-m
tmux attach-session -t $SESSION