package SkipList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Random;
import java.util.SortedSet;

public class SkipListSet<T extends Comparable<T>> implements SortedSet<T> {

    SkipListSetItem root;
    int values;

    public SkipListSet() {
        root = new SkipListSetItem(null, 1);
        values = 0;
    }

    public SkipListSet(Collection <T> collection){
        this();
        addAll(collection);
    }
    


    //================SkipListSetItem Class==================

    private class SkipListSetItem {
        T payload; //payload is the item being stored in our SkipList it is of generic type T
        int height; //height of what our node is going to be
        ArrayList<SkipListSetItem> next; //decalring ArrayLists for our next and previous pointers. As we randomly generate height of an itemWrapper each index of ArrayList contains the next links at that level
        ArrayList<SkipListSetItem> prev; //same thing with prev except this one will just have all the links before (doubly linked list)

        public SkipListSetItem(T payload){
            this.payload = payload; //when a SkipListSetItem gets intialized user will pass in a payload so we will set that payload equal to ours
            height = randomHeight();
            next = new ArrayList<>(height);
            prev = new ArrayList<>(height);

            for(int i = 0; i < height; i++){
                next.add(null);
                prev.add(null);

            }
        }

        public SkipListSetItem(T payload, int rootHeight){ //made this constructor so you can make a node of desired height (only have it here for my dummy root node)
            this.payload = payload;
            height = rootHeight;
            next = new ArrayList(rootHeight);
            next = new ArrayList(rootHeight);

            next.add(null);
            prev.add(null);
        }

        private int randomHeight(){
            int height = 1;
            int maxHeight = 16;

            Random random = new Random();
            
            while(height <= maxHeight && random.nextBoolean()){
                height++;
            }
        
            return height;
        
        }

    } 

    //================END OF SkipListSetItem Class==================

    //================Iterator Class==================
    private class SkipListSetIterator implements Iterator<T>{

        @Override
        public boolean hasNext() {
            // TODO Auto-generated method stub
            throw new UnsupportedOperationException("Unimplemented method 'hasNext'");
        }

        @Override
        public T next() {
            // TODO Auto-generated method stub
            throw new UnsupportedOperationException("Unimplemented method 'next'");
        }

    }

    //================End of Iterator Class==================


    //================Below are all functions pertaining to SortedSet==================
    @Override
    public int size() {
        return values;
    }

    @Override
    public boolean isEmpty() {
        if(root == null)
            return true;
        else
            return false;
    }

    /*
     * For the contains function since it is from the Set interface it is passed in any "Object o" but we need to make sure that object is an instance of the comparable T our class is restrcited to.
     * This function checks if an item is in our skipList or not.
     */
    @Override
    public boolean contains(Object o) {
        
        if(!(o instanceof Comparable<?>)){ //if the object we pass in is not an instanceOf Comparable (We know T extends comparable) then we know the object that got passed in CANT be in our list. We dont put instanceOf T since Java doesnt know what T is at run time
            return false;
        }
        
        @SuppressWarnings("unchecked") //add this so it doesnt give us a warning for casting o to T 
        T element = (T) o;
    
            SkipListSetItem current = root; //start traversing from the root

            for(int i = root.height - 1; i >= 0 ; i--){ //always begin from the top levels down since array lists are zero based index make our i the top level which is root.height - 1 and decrease as we go down the levels
                
                while(current.next.get(i) != null && current.next.get(i).payload.compareTo(element) < 0){ //loop again so long as the next of the level i we are on isnt null and the next of the level i we are on has a value < than that of our element move there
                    current = current.next.get(i);
                }

            }


            //once we exit our loop it is because we reached lowest level and we have to move current one last time because above loop stops one before value we are trying to find
            current = current.next.get(0);
            return current != null && current.payload.compareTo(element) == 1;

    }

    @Override
    public Iterator<T> iterator() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'iterator'");
    }

    @Override
    public Object[] toArray() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'toArray'");
    }

    @Override
    public <T> T[] toArray(T[] a) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'toArray'");
    }


    //Function will add an item "e" to our skip list
    @Override
    public boolean add(T e) {

        if(contains(e)) //since it is a SortedSet NO duplicates use contains function to check if element is in skip list if it is just return false
            return false;

        SkipListSetItem newItem = new SkipListSetItem(e); //create a new item since we will add it further down the line
        SkipListSetItem current = root; //have a current pointer point to root will use it to traverse

        if(root.height < newItem.height){ //updating our dummy root node so that its height is always the height of the tallest node

            for(int i = root.height; i < newItem.height; i++){ //loop through our array list of SkipListItem pointers and add null values
                root.next.add(null);
            }

            root.height = newItem.height; //update the root's height

        }

        for(int i = current.height - 1; i >= 0; i--){ //same way we traversed for contains start at the max height of current

            while(current.next.get(i) != null && current.next.get(i).payload.compareTo(e) < 0){ //keep moving right on that height so long as next isnt null and the item to the right is < 0
                current = current.next.get(i);
            }
            
            if(i <= newItem.height - 1){ //in order to avoid an out of bounds exception only update links in our skip list when the height we are on is the same height as the new node we are adding
                newItem.next.set(i, current.next.get(i)); //set the new nodes next equal to whatever our currents next at that level was so we dont lose information 
                current.next.set(i, newItem); //once that is established set currents next at that level equal to our new node
                newItem.prev.set(i, current); //also remember to set the previous at that level for our newItem. Previous points to current
            }

        }

        return true; //return true once all is executed to signify that we succesfully added an item.
    }

    @Override
    public boolean remove(Object o) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'remove'");
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'containsAll'");
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'addAll'");
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'retainAll'");
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'removeAll'");
    }

    @Override
    public void clear() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'clear'");
    }

    @Override
    public Comparator<? super T> comparator() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'comparator'");
    }


    @Override
    public T first() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'first'");
    }

    @Override
    public T last() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'last'");
    }

    //dont have to do anything else for this function leave it as is
    @Override
    public SortedSet<T> subSet(T fromElement, T toElement) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'subSet'");
    }

    //dont have to do anything else for this function leave it as is
    @Override
    public SortedSet<T> headSet(T toElement) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'headSet'");
    }
    
     //dont have to do anything else for this function leave it as is
    @Override
    public SortedSet<T> tailSet(T fromElement) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'tailSet'");
    }

}
