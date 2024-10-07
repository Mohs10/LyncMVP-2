package com.example.Lync.trie;

import com.example.Lync.Entity.Product;

import java.util.ArrayList;
import java.util.List;

public class Trie {
    private final TrieNode root;

    public Trie() {
        root = new TrieNode();
    }

    // Insert a product name into the Trie
    public void insert(String name, Product product) {
        TrieNode node = root;
        for (char ch : name.toCharArray()) {
            node.children.putIfAbsent(ch, new TrieNode());
            node = node.children.get(ch);
        }
        node.isEndOfWord = true;
        node.product = product;  // Store product when reaching end of word
    }

    // Search for products by prefix
    public List<Product> searchByPrefix(String prefix) {
        TrieNode node = root;
        List<Product> results = new ArrayList<>();

        // Traverse the Trie according to the prefix
        for (char ch : prefix.toCharArray()) {
            node = node.children.get(ch);
            if (node == null) {
                return results;  // Prefix not found, return empty list
            }
        }

        // Collect all products that match the prefix
        collectAllProducts(node, results);
        return results;
    }

    // Helper method to collect all products from a given TrieNode
    private void collectAllProducts(TrieNode node, List<Product> results) {
        if (node.isEndOfWord) {
            results.add(node.product);
        }
        for (char ch : node.children.keySet()) {
            collectAllProducts(node.children.get(ch), results);
        }
    }
}

