(ns bracketolojy.zip
  (:require [clojure.zip :as zip]))

(defn- leftmost-descendant
  "Given a zipper loc, returns its leftmost descendent (ie, down repeatedly)."
  [loc]
  (if (and (zip/branch? loc) (zip/down loc))
    (recur (zip/down loc))
    loc))

(defn post-order-first
  "Given a root node, returns the first node of a postorder tree walk. See
   comment on postorder-next."
  [loc]
  (leftmost-descendant loc))

(defn post-order-next
  "Moves to the next loc in the hierarchy in postorder traversal. Behaves like
   clojure.zip/next otherwise. Note that unlike with a pre-order walk, the root
   is NOT the first element in the walk order, so be sure to take that into
   account in your algorithm if it matters (ie, call postorder-first first
   thing before processing a node)."
  [loc]
  (if (zip/end? loc) ;; If it's the end, return the end.
    loc
    (if-not (zip/up loc)
      [(zip/node loc) :end]
      ;; Node is internal, so we got to it by having traversed its children.
      ;; Instead, we want to try to move to the leftmost descendant of our
      ;; right sibling, if possible.
      (or (and (zip/right loc) (leftmost-descendant (zip/right loc)))
          ;; There was no right sibling, we must move back up the tree.
          (zip/up loc)))))