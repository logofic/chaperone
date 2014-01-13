#!/bin/bash
# Sets up a tmux session with a headless repl,
# cljsbuild auto runnning and a karma instance
# running as well.

SESSION=chaperone

# if not in tmux, then start a new one. But if you are, ignore it.
if ! [ "$TERM" = "screen" ] && ! [ -n "$TMUX" ]; then
	tmux new-session -d -s $SESSION
fi

tmux send-keys "vagrant up" C-m
tmux send-keys "karma start" C-m
tmux split-window -h
tmux select-pane -t 1
tmux send-keys "lein with-profile cljs cljsbuild auto" C-m

if ! [ "$TERM" = "screen" ] && ! [ -n "$TMUX" ]; then
	tmux attach-session -t $SESSION
fi