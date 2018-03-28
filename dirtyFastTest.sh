#!/bin/bash

time ./sbt web/nonGui:test && ./sbt web/gui:test