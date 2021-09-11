{ pkgs ? import <nixpkgs> { } }:

with pkgs;

mkShell { buildInputs = [ clojure clj-kondo leiningen nodejs ]; }
