/*
 * Java Genetic Algorithm Library (@__identifier__@).
 * Copyright (c) @__year__@ Franz Wilhelmstötter
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Author:
 *    Franz Wilhelmstötter (franz.wilhelmstoetter@gmx.at)
 */
package org.jenetix.util;

import static java.util.Collections.singletonList;
import static java.util.Objects.requireNonNull;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Breath first iterator of the tree.
 */
final class BreadthFirst<T> implements Iterator<TreeNode<T>> {
	private final Queue<Iterator<TreeNode<T>>> _queue = new LinkedList<>();

	BreadthFirst(final TreeNode<T> root) {
		requireNonNull(root);
		_queue.add(singletonList(root).iterator());
	}

	@Override
	public boolean hasNext() {
		return !_queue.isEmpty() && _queue.peek().hasNext();
	}

	@Override
	public TreeNode<T> next() {
		final Iterator<TreeNode<T>> it = _queue.peek();
		final TreeNode<T> node = it.next();
		final Iterator<TreeNode<T>> children = node.childIterator();

		if (!it.hasNext()) {
			_queue.poll();
		}
		if (children.hasNext()) {
			_queue.add(children);
		}
		return node;
	}

}