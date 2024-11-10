/*
 * Composed by:
 * Kevin Pereda
 * COP3503C Tues, Thurs 6:00-7:15p.m
 * Dr.Gerber
 */

package SkipList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.SortedSet;

public class SkipListSet<T extends Comparable<T>> implements SortedSet<T> {

    private  SkipListSetItem root;
    private int values;
    private int listMaxHeight; //function will keep track of the maxHeight one node can have in our skipList

    public SkipListSet() {
        root = new SkipListSetItem(null, 1); //we initialize our root to have a payload of null because we are going to make it a dummy node. Also will start with an initial height of 1
        listMaxHeight = 3; //setting maxHeight to initially be 3 if we set it to 1 there might be some thrashing just have like some leeway for a treshold
        values = 0; //when skiplist is first made it has zero values
    }

    public SkipListSet(Collection <T> collection){ //this constuctor is if user wants to make an already established collection into a SkipList
        this();
        addAll(collection);
    }
    


    //================SkipListSetItem Class==================

    private class SkipListSetItem {
        private T payload; //payload is the item being stored in our SkipList it is of generic type T
        private int height; //height of what our node is going to be
        private ArrayList<SkipListSetItem> next; //decalring ArrayLists for our next and previous pointers. As we randomly generate height of an itemWrapper each index of ArrayList contains the next links at that level
        private ArrayList<SkipListSetItem> prev; //same thing with prev except this one will just have all the links before (doubly linked list)

        public SkipListSetItem(T payload){
            this.payload = payload; //when a SkipListSetItem gets intialized user will pass in a payload so we will set that payload equal to ours
            height = randomHeight();
            System.out.println("height of node  "+ payload+" is " + height);
            System.out.println("max heighht is currently "+ listMaxHeight);
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
            prev = new ArrayList(rootHeight);

            next.add(null);
            prev.add(null);
        }

        private int randomHeight(){
            int height = 1;

            Random random = new Random();
            
            while(height < listMaxHeight && random.nextBoolean()){
                height++;
            }
        
            return height;
        
        }

    } 

    //================END OF SkipListSetItem Class==================

    //================Iterator Class==================
    /* **Class below just helps with printing the actual payload items of our skiplist dont have to worry about levels we just
        traverse the lowest levels since we need to print each value** */
    private class SkipListSetIterator implements Iterator<T>{

        private SkipListSetItem current;
        private SkipListSetItem lastReturned; //will use this to keep track of what item we are deleting

        public SkipListSetIterator(){
            current = root.next.get(0); //setting current to start one after the root since my root is a dummy root and the payload item is null I dont want user to see that
        }

        @Override
        public boolean hasNext() { //function checks if theres a next value
            if(current != null) //not null there is a next value
                return true; //return true
            else
                return false; //null return false
            
        }

        @Override
        public T next() { //function reutrns the payload item in node and moves to the next one

            if(!hasNext()){
                throw new NoSuchElementException(); //if the skiplist doesnt have a next throw an exception saying there is no next. No point in returning a payload if there isnt a next
            }
            
            T payload = current.payload; //store the value of the payload on the current node so we can return it later
            lastReturned = current; //remove function below operates by removin the last item current returned. We need to keep a reference to it because below we move the pointer to the next item to prepare it for the next stage
            current = current.next.get(0); //move the current one thing to the right
            return payload; //return the payload item
        }
        
        @Override
        public void remove() {
            if(lastReturned == null) //if lastReturned isnt pointing to an node its because remove was already called
                throw new IllegalStateException("Cannot call remove() back to back. Need to call a next() so that remove knows which value to delete");

                //we want to just call remove method we already have set up that properly rearranges the references and removes a method by travering the list top down

                SkipListSet.this.remove(lastReturned.payload); //have to use this here in order to call remove since SkipListSet isnt static we need to tell java we want to use remove method in the "this instance" that SkipListSetIterator was created
                lastReturned = null; //finally once the item is removed make sure that lastReturned is set to null to indicate the removal and enforce the rule that remove() cant be called again until next() is called again and retrieves a valu
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
        if(root.next.get(0) == null)
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

            for(int i = current.height - 1; i >= 0 ; i--){ //always begin from the top levels down since array lists are zero based index make our i the top level which is root.height - 1 and decrease as we go down the levels
                
                while(current.next.get(i) != null && current.next.get(i).payload.compareTo(element) < 0){ //loop again so long as the next of the level i we are on isnt null and the next of the level i we are on has a value < than that of our element move there
                    current = current.next.get(i);
                }

            }


            //once we exit our loop it is because we reached lowest level and we have to move current one last time because above loop stops one before value we are trying to find
            current = current.next.get(0);
            return current != null && current.payload.compareTo(element) == 0; //check if the current we are on isnt null if it exists and the item in payload is equal to the element we are searching for return true cause it is contained

    }

    //function just creates an instance of our SkipListSetIterator and returns it
    @Override
    public Iterator<T> iterator() {
        return new SkipListSetIterator(); //just return an instance of our SkipListSetIterator which implemetns Iterator<T>
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

        if(contains(e)){
            return false;
        } //since it is a SortedSet NO duplicates use contains function to check if element is in skip list if it is just return false


        SkipListSetItem newItem = new SkipListSetItem(e); //create a new item since we will add it further down the line
        SkipListSetItem current = root; //have a current pointer point to root will use it to traverse

        if(root.height < newItem.height){ //updating our dummy root node so that its height is always the height of the tallest node

            for(int i = root.height ; i < newItem.height; i++){ //loop through our array list of SkipListItem pointers and add null values
                root.next.add(null);
                root.prev.add(null);
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

        values++; //make sure once you add a value successfully you increment values
        updateMaxHeight(); //once we add value to list we need to check and see if the list's max height needs to be updated in order to remain efficient
        return true; //return true once all is executed to signify that we succesfully added an item.
    }

    @Override
    public boolean remove(Object o) {
        
        if(!(o instanceof Comparable<?>)) //if the object we are going to remove doesnt implement comparable than we cant compare it automatically return false signaling we couldnt delete the value
            return false;

        if(!(contains(o))) //also return false if the element user is trying to remove isnt even in the skip list saves us a lot of time.
            return false; 

        @SuppressWarnings("unchecked")
        T e = (T) o; //since we already checked if o implemented the comparable interface we can safely cast o to T since T does implement the comparable interface

        SkipListSetItem current = root; //have a current pointer point to root will use it to traverse

        for(int i = current.height - 1; i >= 0; i--){ //same way we traversed for contains start at the max height of current
    
            while(current.next.get(i) != null && current.next.get(i).payload.compareTo(e) < 0){ //keep moving right on that height so long as next isnt null and the item to the right is < 0
                current = current.next.get(i);
            }
            
            //once we exit the loop that goes to the right if the node we wish to delete is right next to us update the pointers for that level. Make sure before that thought that it isnt null if its null dont do anything cause theres no pointers to adjust at that level i
            if(current.next.get(i) != null && current.next.get(i).payload.compareTo(e) == 0){

                current.next.set(i, current.next.get(i).next.get(i)); //delete the pointer at that level simply by making our currents next = to its next next and skipping over the node

                if(current.next.get(i) != null) //before adjusting the previous make sure we even have a next because if we skipped over a node our next could be pointing to null now
                    current.next.get(i).prev.set(i, current); //make sure to update the previous afterwards
            }
        }
    
        //after we delete from our tree the dummy root node's height could need some readjusting if the node we deleted was the tallest node in the list
        int maxHeight = findMaxHeight() - 1;

        if(root.height - 1  > maxHeight){//if our root height is bigger than the highest height in the skip list we need to adjust root height so it is the same as this new highest height

            System.out.println("Inside adjusting root");
            for(int i = root.height - 1; i > maxHeight; i--){ //loop from our roots height - 1 (zero based indexing) so long as i > than the current maxheight
                root.next.remove(i); //remove the index in the next array list because there is nothing to point to at that level
                root.prev.remove(i); //same thing with the prev array list
            }
            root.height = maxHeight + 1;

            System.out.println("root height after adjustment" + root.height);
        }

        values--; //make sure once you delete a value successfully you decrement our values variable
        updateMaxHeight(); //once we remove value from list we need to check and see if the list's max height needs to be updated in order to remain efficient
        return true; //return true once all is executed to signify that we succesfully removed an item.
        
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


    //==============Below this is all my personal helper functions=================

    public void detailedPrint(){

        SkipListSetItem current = root.next.get(0);

        while(current.next.get(0) != null){
            System.out.print ("Payload: " +current.payload +" Height: " + current.height+" -> ");
            current = current.next.get(0);
        }
    }

    public int findMaxHeight(){

        int maxHeight = -1;
        SkipListSetItem current = root.next.get(0);

        while(current != null){
            if(current.height > maxHeight)
                maxHeight = current.height;
                current = current.next.get(0);
        }
        
        return maxHeight;
    }

    //function dynamically updates our maxHeight of our skip list depending on whether or not the list has grown to a power of 2, so for example 2,4,8,16,32 but it wont update maxHeight if we dont go past a certain treshold which is 3
    public void updateMaxHeight(){

        /* Below line is calcualting the new maxHeight dependant on how many times do I need to multiply 2 by itself to get close to values. 
        If we get 3.75 for that division it means we have more or less an amount of values is = to 2 to the power of 3.75. We use math.ceiling
        to always round up and then compare it with our treshold. If it is greater than the treshold we want to update max height. If it isnt
        do nothing so we have no thrashing*/

        int newMaxHeight = Math.max(3, (int) Math.ceil(Math.log(values) / Math.log(2))); 

        if (newMaxHeight != listMaxHeight) { //if the new maxHeight is the same as our current listMaxHeight then it means we arent at a power of two yet that requires updating. So if they arent equal update maxHeight
            listMaxHeight = newMaxHeight;
        }

    }
}
