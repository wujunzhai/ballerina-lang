/*
 *  Copyright (c) 2020, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package io.ballerinalang.compiler.syntax.tree;

import io.ballerinalang.compiler.internal.parser.tree.STNode;

public class ObjectTypeDescriptorNode extends NonTerminalNode {

    public ObjectTypeDescriptorNode(STNode node, int position, NonTerminalNode parent) {
        super(node, position, parent);
    }

    public Node qualifiers() {
        return childInBucket(0);
    }

    public Token objectKeyword() {
        return childInBucket(1);
    }

    public Token openBrace() {
        return childInBucket(2);
    }

    public Node members() {
        return childInBucket(3);
    }

    public Token closeBrace() {
        return childInBucket(4);
    }

    @Override
    public void accept(SyntaxNodeVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public <T> T apply(SyntaxNodeTransformer<T> visitor) {
        return visitor.transform(this);
    }
}
