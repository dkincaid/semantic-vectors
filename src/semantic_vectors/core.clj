(ns semantic-vectors.core
  (:import (org.apache.lucene.demo IndexFiles)
           (pitt.search.semanticvectors BuildIndex FlagConfig
                                        VectorSearcher$VectorSearcherCosine
                                        VectorStoreRAM)))

(defn index-directory
  "Create a Lucene index from all the documents in a given directory."
  [docs-path index-path]
  (IndexFiles/main (into-array ["-docs" docs-path "-index" index-path])))

(defn create-vectors
  "Create term and document vectors from the given Lucene index. Vectors
file is written to termvectors.bin"
  [index-path]
  (BuildIndex/main (into-array ["-luceneindexpath" index-path])))

(defn read-vector-store
  "Read a VectorStore into RAM from the given file."
  ([f flag-config]
     (VectorStoreRAM/readFromFile flag-config f))
  ([f]
     (read-vector-store f (FlagConfig/getFlagConfig nil))))

(defn vector-searcher
  "Search a vector store using the given query term. You can optionally
pass in the number of nearest neighbors to return (defaults to 10)."
  ([vector-store knn term]
     (let [searcher (VectorSearcher$VectorSearcherCosine.
                     vector-store
                     vector-store
                     nil
                     (FlagConfig/getFlagConfig nil)
                     (into-array String [term]))]
       (.getNearestNeighbors searcher knn)))
  ([vector-store term]
     (vector-searcher vector-store 10 term)))

(defn display-results
  "Display the searcher results as score, term vectors."
  [results]
  (map #(vector (.getScore %)
                (-> % .getObjectVector .getObject)) results))

(defn search-and-display
  "Search a vector store using the given query term and display the
results as score, term vectors."
  [vector-store term]
  (display-results (vector-searcher vector-store term)))


