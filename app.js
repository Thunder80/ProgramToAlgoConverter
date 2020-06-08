const datatypes = ['int', 'float', 'char', 'void', 'long', 'double'];
const operators = ['+', '-', '/', '*', '^', '&', '!'];
let inputArray;
let line = [];
const error = (i) =>
    `There is some syntax error in the given code in line no. ${line[i]} or you have given a syntax which is too unconventional`;
function convert() {
    let inputArea = document.getElementById('input');
    let outputArea = document.getElementById('output');
    line = [];

    let input = format(inputArea.value) + ' ';
    inputArea.value = inputArea.value.replace(/\/\/.*\s/g, '');
    inputArea.value = inputArea.value.replace(/\/\*.*\*\//g, '');
    inputArray = input.split(' ');
    inputArray = inputArray.filter(
        (item) => item.trim() !== '' || item === '\n'
    );
    inputArray = getBrackets(inputArray);
    setLines(inputArray);
    console.log(inputArray);

    let output = evaluate(inputArray);
    outputArea.value = output;
}

function format(s) {
    let formattedString = '';
    s = s.replace(/\/\/.*\s/g, '');
    s = s.replace(/\/\*.*\*\//g, '');
    let j = 0;
    for (let i = 0; i < s.length; i++) {
        if (validCharacter(s.charCodeAt(i))) {
            formattedString += s.charAt(i);
        } else if (s.charAt(i) == '-' && s.charAt(i + 1) == '>') {
            formattedString += ' -> ';
            i++;
        } else {
            formattedString += ' ' + s.charAt(i) + ' ';
        }
    }
    return formattedString;
}

function setLines(a) {
    let j = 1;
    for (let i = 0; i < a.length; i++) {
        if (a[i] == '\n') j++;
        line[i] = j;
    }

    console.log(line);
}

function getBrackets(a) {
    const bracketStatements = ['if', 'else', 'for', 'while'];
    for (let i = 0; i < a.length; i++) {
        if (bracketStatements.includes(a[i])) {
            let o = a.indexOf(')', i) + 1;
            if (a[i] === 'else') o = i + 1;
            let set = false;
            while (a[o] !== '{') {
                if (a[o] !== '\n') {
                    set = true;
                    break;
                }
                o++;
            }
            if (set) {
                a.splice(o, 0, '{');
                i = o;
                a.splice(a.indexOf(';', o) + 1, 0, '}');
            }
        }
    }
    return a;
}

function fixExpression(s) {
    if (s.includes('+=')) {
        let i = s.indexOf('+');
        let v = s.slice(0, i);
        s = v + ' := ' + v + ' + ' + s.slice(i + 2);
    } else if (s.includes('-=')) {
        let i = s.indexOf('-');
        let v = s.slice(0, i);
        s = v + ' := ' + v + ' - ' + s.slice(i + 2);
    } else if (s.includes('*=')) {
        let i = s.indexOf('*');
        let v = s.slice(0, i);
        s = v + ' := ' + v + ' * ' + s.slice(i + 2);
    } else if (s.includes('/=')) {
        let i = s.indexOf('/');
        let v = s.slice(0, i);
        s = v + ' := ' + v + ' / ' + s.slice(i + 2);
    } else if (s.includes('%=')) {
        let i = s.indexOf('%');
        let v = s.slice(0, i);
        s = v + ' := ' + v + ' % ' + s.slice(i + 2);
    } else if (s.includes('++')) {
        let i = s.indexOf('+');
        let v = s.slice(0, i);
        s = v + ' := ' + v + ' + 1';
    } else if (s.includes('--')) {
        let i = s.indexOf('-');
        let v = s.slice(0, i);
        s = v + ' := ' + v + ' - 1';
    } else if (s.includes('=')) {
        s = s.replace('=', ' := ');
    }
    return s;
}

function validCharacter(c) {
    if (
        (c >= 65 && c <= 90) ||
        (c >= 97 && c <= 122) ||
        (c >= 48 && c <= 57) ||
        c == 95 ||
        c == 46
    )
        return true;
    else return false;
}

function validToken(s) {
    for (let i = 0; i < s.length; i++) {
        if (!validCharacter(s.charCodeAt(i))) return false;
    }
    return true;
}

function evaluate(input) {
    let steps = [1],
        output = '',
        stepCounter = 0;
    let blocks = [],
        tab = '',
        loops = [];
    let i;
    for (i = 0; i < input.length; i++) {
        //skip directives
        if (input[i] == '#') {
            i = input.indexOf('\n', i + 1);
        }

        //functions
        else if (
            (datatypes.includes(input[i]) &&
                input[i + 1] === '*' &&
                validToken(input[i + 2]) &&
                input[i + 3] == '(') ||
            (datatypes.includes(input[i]) &&
                validToken(input[i + 1]) &&
                input[i + 2] == '(')
        ) {
            steps = 1;
            let name = input[i + 1] == '*' ? input[i + 2] : input[i + 1];
            let parameters = input
                .slice(input.indexOf('(', i) + 1, input.indexOf(')', i))
                .join(' ');
            parameters = parameters.replace(/\*/g, '');
            let statement = `\n\nBegin procedure ${name}(${parameters}):-\n`;
            output += statement;
            i = input.indexOf('{', i);
            blocks.push('function');
            steps = [1];
            stepCounter = 0;
        }

        //initializations, declarations
        else if (datatypes.includes(input[i])) {
            let j = i + 1,
                loop = 0,
                statement = '';
            if (input[i] === 'struct') j++;
            while (input[j] !== ';') {
                if (validToken(input[j]) && input[j + 1] === '=') {
                    let endComma = input.indexOf(',', j + 2);
                    let endColon = input.indexOf(';', j + 2);
                    let end =
                        endComma > endColon || endComma == -1
                            ? endColon
                            : endComma;
                    let expression = input.slice(j + 2, end).join(' ');
                    if (statement === '')
                        statement += `SET ${input[j]} := ${expression}`;
                    else statement += `, ${input[j]} := ${expression}`;
                    j = end;
                } else if (
                    input[j] === ',' ||
                    validToken(input[j]) ||
                    input[j] === '*' ||
                    input[j] === '[' ||
                    input[j] === ']'
                ) {
                    j++;
                } else if (input[j] === '\n') {
                    j++;
                } else {
                    return error(i);
                }
                if (j >= input.length || j == -1) {
                    return error(i);
                }
            }
            i = j;
            if (statement !== '') {
                output += `${tab}Step ${steps[stepCounter]}: ${statement}\n`;
                steps[stepCounter]++;
            }
        }

        //only initialization
        else if (validToken(input[i]) && input[i + 1] === '=') {
            let end = input.indexOf(';', i + 2);
            let expression = input.slice(i + 2, end).join(' ');
            if (end == -1) {
                return error(i);
            }
            output += `${tab}Step ${steps[stepCounter]}: SET ${input[i]} := ${expression}\n`;
            i = end;
            steps[stepCounter]++;
        } else if (
            validToken(input[i]) &&
            operators.includes(input[i + 1]) &&
            input[i + 2] === '='
        ) {
            let end = inputArray.indexOf(';', i);
            let last = inputArray.slice(i + 3, end).join(' ');
            let expression = `${input[i]} := ${input[i]} ${
                input[i + 1]
            } ${last}`;
            output += `${tab}Step ${steps[stepCounter]}: SET ${expression}\n`;
            i = end;
            steps[stepCounter]++;
        }

        //array initializations
        else if (validToken(input[i]) && input[i + 1] === '[') {
            let equals = input.indexOf('=', i);
            let colon = input.indexOf(';', i);
            let variable = input.slice(i, equals).join(' ');
            let exp = input.slice(equals + 1, colon).join(' ');
            output += `${tab}Step ${steps[stepCounter]}: SET ${variable} := ${exp}\n`;
            steps[stepCounter]++;
            i = colon;
        }

        //pointer initializations
        else if (validToken(input[i]) && input[i + 1] === '->') {
            let end = input.indexOf(';', i + 4);
            let expression = input.slice(i + 4, end).join(' ');
            if (end == -1) {
                return error(i);
            }
            output += `${tab}Step ${steps[stepCounter]}: SET ${input[i]}->${
                input[i + 2]
            } := ${expression}\n`;
            i = end;
            console.log();
            steps[stepCounter]++;
        }

        //unary operators initialization
        else if (
            validToken(input[i]) &&
            input[i + 1] == '+' &&
            input[i + 2] == '+'
        ) {
            output += `${tab}Step ${steps[stepCounter]}: SET ${input[i]} := ${input[i]} + 1\n`;
            steps[stepCounter]++;
            i += 3;
        } else if (
            validToken(input[i]) &&
            input[i + 1] == '-' &&
            input[i + 2] == '-'
        ) {
            output += `${tab}Step ${steps[stepCounter]}: SET ${input[i]} := ${input[i]} - 1\n`;
            steps[stepCounter]++;
            i += 3;
        }

        //structures
        else if (input[i] === 'struct') {
            if (validToken(input[i + 1])) datatypes.push(input[i + 1]);
            else {
                return error(i);
            }
            let j = i + 2;
            let declaration = false;
            while (input[j] != '{') {
                if (input[j] !== '\n') {
                    i = input.indexOf(';', j);
                    declaration = true;
                    break;
                }
                j++;
            }
            if (!declaration) {
                j++;
                statement = `An ADT with the name of ${
                    input[i + 1]
                } has been declared with the following fields: `;
                let type = '';
                let comma = false;
                while (input[j] !== '}') {
                    if (input[j] == ';' || input[j] == '\n' || input[j] == ',')
                        j++;
                    else if (datatypes.includes(input[j])) {
                        type = input[j];
                        j++;
                    } else if (input[j] === 'struct') {
                        type = input[j + 1];
                        if (input[j + 2] === '*') j = j + 3;
                        else j = j + 2;
                    } else if (input[j] === '*') j++;
                    else if (validToken(input[j])) {
                        if (comma) statement += ', ';
                        statement += `'${input[j]}' of type ${type}`;
                        comma = true;
                        j++;
                    } else {
                        return error(i);
                    }
                }
                output = statement + '\n' + output;
                i = j + 1;
            }
        }

        //closing brackets
        else if (input[i] === '}') {
            let block = blocks.pop();
            if (block === 'function') output += `End procedure\n`;
            else if (block === 'if') {
                steps.pop();
                stepCounter--;
                tab = '';
                for (let k = 0; k < stepCounter; k++) tab += '\t';
                output += tab + 'End of if block\n';
            } else if (block == 'else') {
                steps.pop();
                stepCounter--;
                tab = '';
                for (let k = 0; k < stepCounter; k++) tab += '\t';
                output += tab + 'End of else block\n';
            } else if (block == 'elif') {
                steps.pop();
                stepCounter--;
                tab = '';
                for (let k = 0; k < stepCounter; k++) tab += '\t';
                output += tab + 'End of else if block\n';
            } else if (block == 'for') {
                let increment = loops.pop();
                increment = fixExpression(increment);
                output += `${tab}Step ${steps[stepCounter]}: SET ${increment}\n`;
                let pos = loops.pop();
                output =
                    output.substring(0, pos) +
                    steps[stepCounter] +
                    output.substring(pos);
                steps.pop();
                stepCounter--;
                tab = '';
                for (let k = 0; k < stepCounter; k++) tab += '\t';

                output += tab + 'End of for block\n';
            } else if (block == 'while') {
                let pos = loops.pop();
                output =
                    output.substring(0, pos) +
                    (steps[stepCounter] - 1) +
                    output.substring(pos);
                steps.pop();
                stepCounter--;
                tab = '';
                for (let k = 0; k < stepCounter; k++) tab += '\t';

                output += tab + 'End of while block\n';
            }
        } else if (input[i] === 'if') {
            let condition = input
                .slice(input.indexOf('(', i) + 1, input.indexOf(')', i))
                .join(' ');
            condition = condition.replace(/= =/g, '=');
            condition = condition.replace(/& &/g, 'and');
            condition = condition.replace(/\| \|/g, 'or');
            let statement = `if ${condition} then :`;
            output += `${tab}Step ${steps[stepCounter]}: ${statement}\n`;
            steps[stepCounter]++;
            steps.push(1);
            stepCounter++;
            blocks.push('if');
            tab += '\t';
            i = input.indexOf('{', i);
        } else if (input[i] == 'else') {
            if (input[i + 1] == 'if') {
                let condition = input
                    .slice(input.indexOf('(', i) + 1, input.indexOf(')', i))
                    .join(' ');
                condition = condition.replace(/= =/g, '=');
                condition = condition.replace(/& &/g, 'and');
                condition = condition.replace(/\| \|/g, 'or');
                let statement = `else if ${condition} then :`;
                output += `${tab}Step ${steps[stepCounter]}: ${statement}\n`;
                steps[stepCounter]++;
                steps.push(1);
                stepCounter++;
                blocks.push('elif');
                tab += '\t';
                i = input.indexOf('{', i);
            } else {
                let statement = `else :`;
                output += `${tab}Step ${steps[stepCounter]}: ${statement}\n`;
                steps[stepCounter]++;
                steps.push(1);
                stepCounter++;
                blocks.push('else');
                tab += '\t';
                i = input.indexOf('{', i);
            }
        } else if (input[i] === 'for') {
            let statement = `Repeat step 1 to  `;
            let start = input.indexOf(';', i) + 1;
            let end = input.indexOf(';', start) + 1;
            let condition = input.slice(start, end).join(' ');
            let increment = input.slice(end, input.indexOf(')', end));
            condition = condition.replace(/= =/g, '=');
            condition = condition.replace(/& &/g, 'and');
            condition = condition.replace(/\| \|/g, 'or');
            output += `${tab}Step ${steps[stepCounter]}: ${statement}`;
            loops.push(output.length - 1);
            loops.push(increment.join(''));
            blocks.push('for');
            output += `while ${condition}\n`;
            tab += '\t';
            steps.push(1);
            stepCounter++;
            i = input.indexOf('{', end);
        } else if (input[i] === 'while') {
            let condition = input
                .slice(input.indexOf('(', i) + 1, input.indexOf(')', i))
                .join(' ');
            condition = condition.replace(/= =/g, '=');
            condition = condition.replace(/& &/g, 'and');
            condition = condition.replace(/\| \|/g, 'or');
            let statement = 'Repeat step 1 to  ';
            output += `${tab}Step ${steps[stepCounter]}: ${statement}`;
            loops.push(output.length);
            blocks.push('while');
            output += ` while ${condition}\n`;
            tab += '\t';
            steps.push(1);
            stepCounter++;
            i = input.indexOf('{', i);
        }
        //print
        else if (input[i] === 'printf') {
            let comma = input.indexOf(',', i);
            let brac = input.indexOf(';', i);
            if (comma < brac && comma != -1)
                output +=
                    `${tab}Step ${steps[stepCounter]}: PRINT ` +
                    input.slice(comma + 1, brac - 1).join(' ') +
                    '\n';
            else
                output +=
                    `${tab}Step ${steps[stepCounter]}: PRINT ` +
                    input.slice(i + 3, brac - 2).join(' ') +
                    '\n';
            steps[stepCounter]++;
            i = brac;
        } else if (input[i] === 'typedef') {
            let temp = input.indexOf(';', i);
            datatypes.push(input[temp - 1]);
            i = temp;
        } else if (input[i] === 'return') {
            let temp = input.indexOf(';', i);
            let exp = input.slice(i + 1, temp).join(' ');
            output += `${tab}Step ${steps[stepCounter]}: RETURN ${exp}\n`;
            steps[stepCounter]++;
            i = temp;
        } else if (input[i] === 'scanf') {
            let brac = input.indexOf(')', i);
            let exp = input
                .slice(input.indexOf(',', i) + 1, brac)
                .join(' ')
                .replace(/&/g, '');
            output += `${tab}Step ${steps[stepCounter]}: READ ` + exp + '\n';
            steps[stepCounter]++;
            i = brac + 1;
        }

        //function calling
        else if (validToken(input[i]) && input[i + 1] === '(') {
            i = input.indexOf(';', i);
        }
        //free pass
        else if (input[i] === '*') {
        }

        //keeping track of line number to print in error messages
        else if (input[i] === '\n') {
        } else if (input[i] == 'do') {
            return 'do-while loops not supported because I hate it';
        } else if (input[i] == 'switch') {
            return 'Switch cases are not supported because I hate it';
        } else {
            {
                return error(i);
            }
        }
    }
    return output;
}
