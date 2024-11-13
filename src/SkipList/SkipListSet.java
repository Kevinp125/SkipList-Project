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
import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.Set;
import java.util.SortedSet;

public class SkipListSet<T extends Comparable<T>> implements SortedSet<T> {

    private  SkipListSetItem root;
    private int values;
    private int listMaxHeight; //function will keep track of the maxHeight one node can have in our skipList

    //SkipListSet constructors
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

    //function returns an Array version of our colletion with the general type object
    @Override
    public Object[] toArray() {
    
        Object [] collectionArray = new Object [size()]; //make a new array the size of our skipListSet

        Iterator<T> iterator = iterator(); //declare an iterator to go through our SkipListSet

        int i = 0; //counter for the indexing of our array

        while(iterator.hasNext()){
            Object element = iterator.next(); //store the element from skipList
            collectionArray[i++] = element; //add it to our array
        }

        return collectionArray; //finally return this array
    }

    //funciton does same as function above except it takes in a specified type array and adds the collection elements into that array aligning with its type. Then it returns it
    @SuppressWarnings("unchecked")
    @Override
    public <T> T[] toArray(T[] a) {
        
        if(a.length < size()){
            a = (T[]) java.lang.reflect.Array.newInstance(a.getClass().getComponentType(), size()); //looks like a complicated line but from what I found a generic array cant be created normally since Java doesnt know type T is at compile time. Need to use reflection to retrieve types of elements stored in A and make a new instance with that type and size
        }

        //if the size of the array passed in was big enough to store all of our skiplistset elements just fill the indexes up

        Iterator<T> iterator = (Iterator<T>) iterator();

        int i = 0;

        while(iterator.hasNext()){
            T element = iterator.next();
            a[i++] = element;
        }

        return a;
    }


    //Function will add an item "e" to our skip list
    @Override
    public boolean add(T e) {

        // if(contains(e)){
        //     return false;
        // } //since it is a SortedSet NO duplicates use contains function to check if element is in skip list if it is just return false


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
            
            if (current.next.get(i) != null && current.next.get(i).payload.compareTo(e) == 0) {
                return false; // Element already exists, so skip insertion
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
        
        boolean found = false;
        int maxHeight = -1;
        SkipListSetItem nodeBeingDeleted = null; 

        if(!(o instanceof Comparable<?>)) //if the object we are going to remove doesnt implement comparable than we cant compare it automatically return false signaling we couldnt delete the value
            return false;

        // if(!(contains(o))) //also return false if the element user is trying to remove isnt even in the skip list saves us a lot of time.
        //     return false; 

        @SuppressWarnings("unchecked")
        T e = (T) o; //since we already checked if o implemented the comparable interface we can safely cast o to T since T does implement the comparable interface

        SkipListSetItem current = root; //have a current pointer point to root will use it to traverse

        for(int i = current.height - 1; i >= 0; i--){ //same way we traversed for contains start at the max height of current

            while(current.next.get(i) != null && current.next.get(i).payload.compareTo(e) < 0){ //keep moving right on that height so long as next isnt null and the item to the right is < 0
                current = current.next.get(i);
            }
            
            if(current.payload != null && current.height > maxHeight) //check to update our maxHeight to update our root height after we only want the height of the nodes that ARENT dummy nodes because current when first starting removal is alreayd at max Height
                maxHeight = current.height;

            //once we exit the loop that goes to the right if the node we wish to delete is right next to us update the pointers for that level. Make sure before that thought that it isnt null if its null dont do anything cause theres no pointers to adjust at that level i
            if(current.next.get(i) != null && current.next.get(i).payload.compareTo(e) == 0){
                found = true;
                nodeBeingDeleted = current.next.get(i);
                current.next.set(i, current.next.get(i).next.get(i)); //delete the pointer at that level simply by making our currents next = to its next next and skipping over the node

                if(current.next.get(i) != null) //before adjusting the previous make sure we even have a next because if we skipped over a node our next could be pointing to null now
                    current.next.get(i).prev.set(i, current); //make sure to update the previous afterwards
            }
        }
        
        if(!found){
            return false;
        }
        

        //after we delete from our tree the dummy root node's height could need some readjusting if the node we deleted was the tallest node in the list
        if(nodeBeingDeleted.height == root.height){//if our root height is bigger than the highest height in the skip list we need to adjust root height so it is the same as this new highest height
            
            root.next.subList(maxHeight, root.height).clear(); // Clear from maxHeight+1 to end
            root.prev.subList(maxHeight, root.height).clear(); // Clear from maxHeight+1 to end
            root.height = maxHeight;  // Adjust root height to match maxHeight
        }

        values--; //make sure once you delete a value successfully you decrement our values variable
        updateMaxHeight(); //once we remove value from list we need to check and see if the list's max height needs to be updated in order to remain efficient
        return true; //return true once all is executed to signify that we succesfully removed an item.
        
    }

    //checks if all the items in the collection are contained within my SkipListSet
    @Override
    public boolean containsAll(Collection<?> c) {
        
        for(Object value: c){ //for every object in collection c 
            if(!contains(value)) //check if that value isnt contained in the SkipListSet if it isnt we return false because it isnt a match
                return false;
        }

        //if we leave loop without returning false its because all the values in the collection are contained in our SkipListSet
        return true;
    }

    //function adds all values that are in collection passed in to our SkipListSet so long as the value isnt already in the SkipListSet
    @Override
    public boolean addAll(Collection<? extends T> c) {
    
        boolean changeInSet = false; //initially set this variable to false the moment we add something new change the flag to true

        for(T element : c){ //for every object in the collection we got passed in (we dont really have to check if the object is of type T because the collection being passed in is of a type that extends T)

            if(!contains(element)){ //if the element isnt in our SkipListSet we want to add it and set flag to true
                add(element);
                changeInSet = true;
            }
        }

        return changeInSet; //return whether our skipListSet was modified or not
    }

    //retainAll essentially modifies our SkipListSet so that it becomes the intersection of whatever collection got passed in so if out SkipListSet was 1,2,4 and collection is 2 our new SkipListSet becomes 2
    @Override
    public boolean retainAll(Collection<?> c) {

        boolean changeInSet = false;

        Iterator<T> iterator = iterator(); //creating an instnace of an iterator of our SkipListSet so we can traverse it

        while(iterator.hasNext()){ //so long as we have a next...
        
            T element = iterator.next(); //store in element what the iterators next is

            if(!(c.contains(element))){ //if that element isnt contained in the collection the collection we got passed in
                iterator.remove(); //remove it from our SkipListSet
                changeInSet = true; //flag that it was changed
            }
                
        }

        return changeInSet;
    }

    //method removes from our SkipListSet all stuff that is in the collections passed in. Pretty much same thing as retains all except there isnt a ! in the if condition
    @SuppressWarnings("unchecked")
    @Override
    public boolean removeAll(Collection<?> c) {

        boolean changeInSet = false; ///declare a boolean to keep track of whether or not our set changed

        for(Object payload : c){ //loop through all the objects in the collection we are passed in

            T element; //declare an element variable since we are going to cast the payload object to a T type further down

            if(payload instanceof Comparable<?>) //make sure that object in colleciton passed in is comparable if it is cast it to T
                element = (T) payload; //cast the arbitrary object in our collection to type T since we verified it is comparable 
            else
                return changeInSet;//if not just reutrn false because we cant remove non comparable items.
            
            
            if(this.contains(element)){ //if our skipList contains the element from the colleciton
                this.remove(element); //remove it from our skipList
                changeInSet = true; //flag our variable to true since our SkipListSet has changed
            }

        }

        return changeInSet; //return this variable

    }

    //funciton just completely wipes out our SkipListSet easy implementation
    @Override
    public void clear() {
        
        root.next.set(0, null); //just make our root  = null and then set values to 0
        values = 0;

    }

    //professor said this one can return null so leave it like that
    @Override
    public Comparator<? super T> comparator() {
        return null;
    }


    //returns first item in SkipListSet
    @Override
    public T first() {
        if(isEmpty())
            throw new NoSuchElementException("SkipListSet is empty"); //if roots next is null its empty so just throw an exception nothing to return

        return root.next.get(0).payload; //otherwise return first item T the payload.

    }

    @Override
    public T last() {

        if (isEmpty()) { //if it is empty no last element to return
            throw new NoSuchElementException("SkipListSet is empty");
        }

        SkipListSetItem current = root;

        for (int i = root.height - 1; i >= 0; i--) {
            while (current.next.get(i) != null) { //so long as there is an express lane take it
                current = current.next.get(i);
            }
        }
    
        return current.payload; // Return the payload of the last node

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

    //function randomizes all the heights in our skipList the approach I will take is just readding all the nodes to a new skiplist essentially
    //so i can just resuse my logic and not reinvent the wheel
    public void reBalance(){

        ArrayList<T> prevSkipListVals = new ArrayList<>(); //decalring an arrayList of type <T> since we need to keep it generic
        
        SkipListSetItem current = root.next.get(0);

        //now in this loop I will loop through our already established SkipListSet and add all of its values into our arrayList so we have all the payloads
        while(current !=  null){
            prevSkipListVals.add(current.payload); //add payload to our array list
            current = current.next.get(0); //move to next item in current skipList
        }

        //once we have all payload values we can reset our current skipListSet since we want to add all values again so that all the heights get randomized

        root = new SkipListSetItem(null, 1); //initialize our root to now point to a dummy node and not the old skipListSet

        for(T payload : prevSkipListVals) //for each payload value in our prevSkipListVals arrayList
            add(payload); //just add that payload and since we reset the root all the heights will be randomized. Bam 

    }
}
