package com.example.Lync.trie;

import com.example.Lync.Entity.Product;

import java.util.HashMap;
import java.util.Map;

class TrieNode {
    Map<Character, TrieNode> children = new HashMap<>();
    boolean isEndOfWord;
    Product product;  // Store the associated Product object at the end of the word
}
