package Assembler;

import java.util.Stack;

public class StackMachine {
    private Stack<Integer> stack = new Stack<>();

    public void execute(String command, Integer operand) {
        switch (instrukcije.valueOf(command)) {
            case PUSH:
                stack.push(operand);
                break;
            case POP:
                if (!stack.isEmpty()) {
                    stack.pop();
                }
                break;
            case ADD:
                if (stack.size() >= 2) {
                    stack.push(stack.pop() + stack.pop());
                }
                break;
            case SUB:
                if (stack.size() >= 2) {
                    stack.push(stack.pop() - stack.pop());
                }
                break;
            case MUL:
                if (stack.size() >= 2) {
                    stack.push(stack.pop() * stack.pop());
                }
                break;
            case DIV:
                if (stack.size() >= 2) {
                    stack.push(stack.pop() / stack.pop());
                }
                break;
        }
    }

    public void printStack() {
        System.out.println(stack);
    }
}