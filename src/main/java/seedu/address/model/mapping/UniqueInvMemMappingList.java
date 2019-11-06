package seedu.address.model.mapping;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.List;
import java.util.ListIterator;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import seedu.address.model.mapping.exceptions.DuplicateMappingException;
import seedu.address.model.mapping.exceptions.MappingNotFoundException;

/**
 * A list of persons that enforces uniqueness between its elements and does not allow nulls.
 * A task is considered unique by comparing using {@code Task#isSameTask(Task)}. As such, adding and updating of
 * persons uses Task#isSameTask(Task) for equality so as to ensure that the task being added or updated is
 * unique in terms of identity in the UniqueTaskList. However, the removal of a task uses Task#equals(Object) so
 * as to ensure that the task with exactly the same fields will be removed.
 * <p>
 * Supports a minimal set of list operations.
 */
public class UniqueInvMemMappingList implements Iterable<InvMemMapping> {

    private final ObservableList<InvMemMapping> internalList = FXCollections.observableArrayList();
    private final ObservableList<InvMemMapping> internalUnmodifiableList =
            FXCollections.unmodifiableObservableList(internalList);
    //private final ArrayList<InvMemMapping> internalList = new ArrayList<>();
    //private final ArrayList<InvMemMapping> internalUnmodifiableList = new ArrayList<>(internalList);
    /*private final ArrayList<Integer> lonelyInv = new ArrayList<>();

    public ArrayList<Integer> getLonelyInv() {
        return lonelyInv;
    }
    private void addLonelyInv(int x) {
        lonelyInv.add(x);
    }*/

    /**
     * Returns true if the list contains an equivalent task as the given argument.
     */
    public boolean contains(InvMemMapping toCheck) {
        requireNonNull(toCheck);
        return internalList.stream().anyMatch(toCheck::isSameMapping);
    }

    /**
     * Adds a task to the list.
     * The task must not already exist in the list.
     */
    public void add(InvMemMapping toAdd) {
        requireNonNull(toAdd);
        if (contains(toAdd)) {
            throw new DuplicateMappingException();
        }
        internalList.add(toAdd);

    }

    /**
     * Replaces the task {@code target} in the list with {@code editedTask}.
     * {@code target} must exist in the list.
     * The task identity of {@code editedTask} must not be the same as another existing task in the list.
     */
    public void setMapping(InvMemMapping target, InvMemMapping editedMapping) {
        requireAllNonNull(target, editedMapping);

        int index = internalList.indexOf(target);
        if (index == -1) {
            throw new MappingNotFoundException();
        }

        if (!target.isSameMapping(editedMapping) && contains(editedMapping)) {
            throw new DuplicateMappingException();
        }

        internalList.set(index, editedMapping);
    }

    /**
     * Removes the equivalent task from the list.
     * The task must exist in the list.
     */
    public void remove(InvMemMapping toRemove) {
        requireNonNull(toRemove);
        if (!internalList.remove(toRemove)) {
            throw new MappingNotFoundException();
        } /*else {
            this.addLonelyInv(toRemove.getInventoryIndex());
        }*/
    }

    public void setMappings(UniqueInvMemMappingList replacement) {
        requireNonNull(replacement);
        internalList.setAll(replacement.internalList);
    }

    /**
     * Replaces the contents of this list with {@code tasks}.
     * {@code tasks} must not contain duplicate tasks.
     */
    public void setMappings(List<InvMemMapping> mappings) {
        requireAllNonNull(mappings);
        if (!mappingsAreUnique(mappings)) {
            throw new DuplicateMappingException();
        }

        internalList.setAll(mappings);
    }

    public void updateMemberRemoved(int index) {
        ListIterator<InvMemMapping> iterator = iterator();
        while (iterator.hasNext()) {
            InvMemMapping mapping = iterator.next();
            int mappingIndex = mapping.getMemberIndex();
            if (mappingIndex == index) {
                iterator.remove();
            } else if (mappingIndex > index) {
                InvMemMapping updatedMapping = new InvMemMapping(mapping.getInventoryIndex(), mappingIndex - 1);
                iterator.remove();
                iterator.add(updatedMapping);
            }
        }
    }

    public void updateInventoryRemoved(int index) {
        ListIterator<InvMemMapping> iterator = iterator();
        while (iterator.hasNext()) {
            InvMemMapping mapping = iterator.next();
            int mappingIndex = mapping.getInventoryIndex();
            if (mappingIndex == index) {
                iterator.remove();
            } else if (mappingIndex > index) {
                InvMemMapping updatedMapping = new InvMemMapping(mappingIndex - 1, mapping.getMemberIndex());
                iterator.remove();
                iterator.add(updatedMapping);
            }
        }
        /*for(int i = 0; i < lonelyInv.size(); i++) {
            if(index == lonelyInv.get(i)) {
                lonelyInv.remove(i);
                i--;
            } else if (lonelyInv.get(i) > index) {
                lonelyInv.set(i, lonelyInv.get(i) - 1);
            }
        }*/
    }

    /**
     * Returns the backing list as an unmodifiable {@code ObservableList}.
     */
    public ObservableList<InvMemMapping> asUnmodifiableObservableList() {
        return internalUnmodifiableList;
    }

    @Override
    public ListIterator<InvMemMapping> iterator() {
        return internalList.listIterator();
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof UniqueInvMemMappingList // instanceof handles nulls
                && internalList.equals(((UniqueInvMemMappingList) other).internalList));
    }

    @Override
    public int hashCode() {
        return internalList.hashCode();
    }

    /**
     * Returns true if {@code tasks} contains only unique tasks.
     */
    private boolean mappingsAreUnique(List<InvMemMapping> mappings) {
        for (int i = 0; i < mappings.size() - 1; i++) {
            for (int j = i + 1; j < mappings.size(); j++) {
                if (mappings.get(i).isSameMapping(mappings.get(j))) {
                    return false;
                }
            }
        }
        return true;
    }

    public ObservableList<ObservableList<InvMemMapping>> getMappings() {
        ObservableList<ObservableList<InvMemMapping>> result = FXCollections.observableArrayList();
        for (InvMemMapping map : internalList) {
            boolean isAdded = false;
            for (ObservableList<InvMemMapping> e : result) {
                if (e.get(0).getMemberIndex() == (map.getMemberIndex())) {
                    e.add(map);
                    isAdded = true;
                }
            }
            if (!isAdded) {
                ObservableList<InvMemMapping> listToAdd = FXCollections.observableArrayList();
                listToAdd.add(map);
                result.add(listToAdd);
            }
        }
        return result;
    }
}
