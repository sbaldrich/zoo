# By default, let's put all templates in a 'templates' directory living with the main script.
export DORIS_TEMPLATE_LOCATION=$(dirname $0:A)/templates

# Just an alias to make things easier to read. 
# Not necessary in OSX
# alias pbcopy='xclip -selection clipboard'

# Util functions
function doris_say(){
  print -P "\n%B%F{yellow}>>%F{white} $@ \n%B%F{yellow}%f%b"
}

function doris_warn(){
  print -P "\n%B%F{red}!%F{white} $@ %B%f%b"
}

function doris_template_exists(){
    if [ ! -f $1 ]; then
        doris_warn "No templates with name '$1' were found!" >&2
        return 1
    fi
}

# Render a template by replacing env variables by their current value.
# No check is performed to see if a variable is currently set.
function doris_render(){
    local template=$DORIS_TEMPLATE_LOCATION/$1
    if  doris_template_exists $template ; then
        doris_say "Rendering Template '$1'"
        local output=$(eval "echo \"$(cat $template)\"")
        echo $output
         [[ $#doris_copy -ge 1 ]] && echo $output | pbcopy  && doris_say "Copied to clipboard!"
    fi
}

# Display a template and highlight its variables.
function doris_show(){
    local template=$DORIS_TEMPLATE_LOCATION/$1
    if  doris_template_exists $template ; then
        doris_say "Displaying template '$1'"
        cat $template | grep --color -E '\$[-A-Z_a-z:{}]+|$'
        [[ $#doris_copy -ge 1 ]] && cat $template | pbcopy && doris_say "Copied to clipboard!"
    fi
}

# Doris driver function
function doris(){
    local doris_command doris_copy
    zparseopts -E -D -- -copy=doris_copy c=doris_copy
    if [[ ! -z $1 ]]; then
        doris_command=$1
        shift
        if [[ -z $1 ]]; then
            doris_warn "command $doris_command expects an argument"
            return -1
        fi
        case "$doris_command" in
            show)
                doris_show $1
                ;;
            render)
                doris_render $1
                ;;
        esac
    else 
        doris_say "Expected a command."
    fi
} 

# Completions for doris
function _doris(){
    local context state state_descr line
    typeset -A opt_args
    
    _arguments -C \
     {-c,--copy}'[Copy the results to the clipboard]' \
    '1:subcommand:((
        show\:"Show template content"
        render\:"Render the template"
    ))' \
    '2:file:->templates' 
    
    case $line[1] in 
        show|render)
            _files -W $DORIS_TEMPLATE_LOCATION
            ;;
    esac
}

compdef _doris doris