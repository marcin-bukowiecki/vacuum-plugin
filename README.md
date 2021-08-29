# Vacuum GoLand plugin

Vacuum is a GoLand plugin which provides code inspections, code metrics and quick fixes for `golang` code.

## Requirements

- GoLand 2021.1 and later
- [golang/lint](https://github.com/golang/lint) if we want to have `golang/lint` support

## Features

#### Syntax inspections:
 - over 30 code inspections and quick fixes  
 - postfix templates for slices
 - postfix templates for maps
 - AWS DynamoDB live templates
 - unhandled error  
 - empty comment inspection
 - empty code blocks
 - indent error flow
 - inverted boolean expression
 - useless operator pairs
 - useless if statements
 - separate statements
 - naming convention
 - missing else block
 - [golang/lint](https://github.com/golang/lint) integration.

#### Code metrics inspections:
 - [Cognitive Complexity](https://www.sonarsource.com/docs/CognitiveComplexity.pdf)
 - boolean expressions complexity
 - number of lines in function
 - number of lines in `go` source file
 - control flow depth

#### Intentions:
 - unit test creator
 - go to test

And many, many more... :)

This plugin tries to cover most code smells, bugs and code review comments from:
 - https://github.com/golang/go/wiki/CodeReviewComments
 - https://rules.sonarsource.com/go

## Installation

You can install this plugin from JetBrains plugin repository.

## Configuration

To use Inspections with `golang/lint` run following command: `go get -u golang.org/x/lint/golint`.

Since `golang/lint` is deprecated it can be disabled from plugin settings (`File` -> `Settings` -> `Tools` -> `Vacuum Settings` and uncheck `Enable golint`).

To disable particular Inspections go to: `File` -> `Settings` -> `Editor` -> `Inspections`, and under `Vacuum` 
group uncheck undesired Inspection.

## Settings

Some metric intentions e.g. number of source file lines are based on default configuration. 
To change this go to: `File` -> `Settings` -> `Tools` -> `Vacuum Settings` and change desired metric.

#### Default metrics:

- Source file lines threshold: `500`
- Switch case lines threshold: `6`
- Function lines threshold: `25`
- Method lines threshold: `25`
- Function number of parameters threshold: `5`
- Number of case branches threshold: `10`
- Cognitive complexity limit: `15`
- Number of boolean expressions: `4`
- Control flow depth: `4`