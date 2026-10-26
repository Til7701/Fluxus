if exists("b:current_syntax")
    finish
endif

syn keyword flxKeyword class let do static as import this new
syn keyword flxBooleans true false
syn keyword flxControlFlow when return
syn match flxNumberConstant "[0-9]\+\(\.[0-9]\+\)\?[IUF][0-9]\+"
syn region flxString start=+"+ skip=+\\."+ end=+"+
syn match flxMethodName "\<[a-z][a-zA-Z_0-9]*\ze("
syn match flxComment "//.*$"


hi def link flxKeyword Keyword
hi def link flxBooleans Boolean
hi def link flxControlFlow Conditional
hi def link flxNumberConstant Number
hi def link flxString String
hi def link flxMethodName Function
hi def link flxComment Comment

let b:current_syntax = "flx"
