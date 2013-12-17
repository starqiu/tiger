#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <time.h>

// The Gimple Garbage Collector.


//===============================================================//
// The Java Heap data structure

/*
          -----------------------------------------------------------
          |                                     |                                |
          -----------------------------------------------------------
          ^\                                  /^
           | \<~~~~~ size ~~~~~>/  |
         from                               to
*/
struct JavaHeap
{
  	int size; // in bytes, note that this is for semi-heap size
  	char *from; // the "from" space pointer
  	char *fromFree; // the next "free" space in the from space
  	char *to; // the "to" space pointer
  	char *toStart; // "start" address in the "to" space
  	char *toNext; // "next" free space pointer in the to space
};

// The Java heap, which is initialized by the following
// "heap_init" function
struct JavaHeap heap;

// log data structure
clock_t start, end; 
float sec;
int gcNum = 0;
int gcByte = 0;

int ComputeSize(void *ptr);
void *Copy(void *ptr);
static void Tiger_gc();

// Lab 4, exercise 10:
// Given the heap size (in bytes), allocate a Java heap
// in the C heap, initialize the relevant fields
void Tiger_heap_init(int heapSize)
{
  	// You should write 7 statement here:
  	// #1: allocate a chunk of memory of size "heapSize" using "malloc"
	struct JavaHeap *temp = (struct JavaHeap *)malloc(heapSize);
 	// #2: initialize the "size" field, note that "size" field
  	// is for semi-heap, but "heapSize" is for the whole heap.
	heap.size = heapSize / 2;
  	// #3: initialize the "from" field (with what value?)
  	heap.from = (char *)temp;
 	// #4: initialize the "fromFree" field (with what value?)
	heap.fromFree = heap.from;
  	// #5: initialize the "to" field (with what value?)
	heap.to = heap.from + heap.size;
  	// #6: initizlize the "toStart" field with NULL;
	heap.toStart = NULL;
  	// #7: initialize the "toNext" field with NULL;
	heap.toNext = NULL;
 	return;
}

// The "prev" pointer, pointing to the top frame on the GC stack
// (see part A of Lab 4)
void *previous = 0;


//===============================================================//
// Object Model And allocation


// Lab 4: exercise 11:
// "new" a new object, do necessary initializations, and
// return the pointer (reference)
/*         --------------------
            | vptr            ---|----> (points to the virtual method table)
            |-------------------|
            | isObjOrArray | (0: for normal objects)
            |-------------------|
            | length             | (this field should be empty for normal objects)
            |-------------------|
            | forwarding      |
            |-------------------|\
   p---->| v_0                | \
            |-------------------|  s
            | ...                   |  i
            |-------------------|  z
            | v_{size-1}      | /e
            --------------------/
*/
// Try to allocate an object in the "from" space of the Java heap
// Read Tiger book chapter 13.3 for details on the allocation
// There are two cases to consider:
//     1. If the "from" space has enough space to hold this object, then
//         allocation succeeds, return the appropriate address (look at the above figure, be careful);
//     2. if there is no enough space left in the "from" space, then
//         you should call the function "Tiger_gc()" to collect garbages.
//         and after the collection, there are still two sub-cases:
//             a: if there is enough space, you can do allocations just as case 1;
//             b: if there is still no enough space, you can just issue
//                 an error message ("OutOfMemory") and exit.
//                 (However, a production compiler will try to expand the Java heap.)
void *Tiger_new(void *vtable, int size)
{
	// Your code here:
	if((heap.from + heap.size) - heap.fromFree < size)
	{
		int before = (heap.from + heap.size) - heap.fromFree;
		start = clock();
		Tiger_gc();
		end = clock();
		sec = (double)(end - start) / CLOCKS_PER_SEC; 
		gcNum++;
		gcByte += (heap.from + heap.size) - heap.fromFree - before;
		FILE *fp;
		fp = fopen("log.txt", "at");
		if (!fp)
		{
			printf("File cannot be opened");
			exit(1);
		} 
		fprintf(fp, "%d round of GC: %fs, collected %d bytes\n", gcNum, sec, gcByte);
		fclose(fp);

		if((heap.from + heap.size) - heap.fromFree < size)
		{
			printf("Error: OutOfMemory!\n");
			exit(1);
		}
	}
	char *temp = heap.fromFree;
	heap.fromFree += size;

	*((void **)temp) = vtable;
	*((int *)temp + 1) = 0;
	*((void **)(temp + 12)) = 0;

	return (void *)temp;
}

// "new" an array of size "length", do necessary
// initializations. And each array comes with an
// extra "header" storing the array length and other information
/*        --------------------
           | vptr                | (this field should be empty for an array)
           |-------------------|
           | isObjOrArray | (1: for array)
           |-------------------|
           | length             |
           |-------------------|
           | forwarding      |
           |-------------------|\
  p---->| e_0                | \
           |-------------------|  s
           | ...                   |  i
           |-------------------|  z
           | e_{length-1}   | /e
           --------------------/
*/
// Try to allocate an array object in the "from" space of the Java heap
// Read Tiger book chapter 13.3 for details on the allocation
// There are two cases to consider:
//     1. If the "from" space has enough space to hold this array object, then
//         allocation succeeds, return the apropriate address (look at the above figure, be careful);
//     2. if there is no enough space left in the "from" space, then
//         you should call the function "Tiger_gc()" to collect garbages.
//         and after the collection, there are still two sub-cases:
//             a: if there is enough space, you can do allocations just as case 1;
//             b: if there is still no enough space, you can just issue
//                 an error message ("OutOfMemory") and exit.
//                 (However, a production compiler will try to expand the Java heap.)
void *Tiger_new_array(int length)
{
	// Your code here:
	if((heap.from + heap.size) - heap.fromFree < (length + 16))
	{
		int before = (heap.from + heap.size) - heap.fromFree;
		start = clock();
		Tiger_gc();
		end = clock();
		sec = (double)(end - start) / CLOCKS_PER_SEC; 
		gcNum++;
		gcByte += (heap.from + heap.size) - heap.fromFree - before;
		FILE *fp;
		fp = fopen("log.txt", "at");
		if (!fp)
		{
			printf("File cannot be opened");
			exit(1);
		} 
		fprintf(fp, "%d round of GC: %fs, collected %d bytes\n", gcNum, sec, gcByte);
		fclose(fp);

		if((heap.from + heap.size) - heap.fromFree < (length + 16))
		{
			printf("Error: OutOfMemory!\n");
			exit(1);
		}
	}
	int *temp = (int *)heap.fromFree;
	heap.fromFree += (length + 16);

	*((void **)temp) = 0;
	*(temp + 1) = 1;
	*(temp + 2) = length / sizeof(int);
	*((void **)(temp + 3)) = 0;

	return (void *)temp;
}

//===============================================================//
// The Gimple Garbage Collector

// Lab 4, exercise 12:
// A copying collector based-on Cheney's algorithm
int ComputeSize(void *ptr)
{
	int size;
	int isObjOrArray = *((int *)ptr + 1);
	if(isObjOrArray == 1)// if it is array
		size = *((char *)ptr + 8) * 4 + 16;
	else// if it is object
	{
		void *vptr = *((char **)ptr);
		char *address_field_gc_map = (char *)(*((char **)vptr));
		int len = strlen(address_field_gc_map);
		size = 16 + 4 * len;
	}
	return size;
}

void *Copy(void *ptr)
{
	if((char *)ptr< (heap.from + heap.size) && (char *)ptr >= heap.from)// if ptr points to fromspace
	{
		void *forwarding =  *((char **)((char *)ptr + 12));
		
		if((char *)forwarding >= heap.to && (char *)forwarding < (heap.to + heap.size))// if forwarding points to tospace, then it has been copied
			return forwarding;
		else if(((char *)forwarding < (heap.from + heap.size) && (char *)forwarding >= heap.from) || forwarding == 0)//  if forwarding points to fromspace, then copy it
		{
			void *temp = heap.toNext;
			*((char **)((char *)ptr + 12)) = (char *)temp;// update its forwarding
			
			// compute the size
			int size = ComputeSize(ptr);

			// begin copying
			int i = 0;
			for(i=0; i<size; i++)
			{
				*((char *)heap.toNext + i) = *((char *)ptr + i);
			}

			heap.toNext += size;

			return temp;
		}
	}
	else
		return ptr;
}

static void Tiger_gc()
{
	// Your code here:
	heap.toStart = heap.to;
	heap.toNext = heap.to;

	// initial collection
	while(previous != 0)
	{
		// arguments
		int *arguments_base_address = (int *)(*((char **)((char *)previous + 8)));
		void *aptr = arguments_base_address;
		void *atemp = aptr;
		char *arguments_gc_map = (char *)(*((char **)((char *)previous + 4)));
		if(arguments_gc_map != 0)
		{
			int len = strlen(arguments_gc_map);
			int i = 0;
			for(i=0; i<len; i++)
			{
				if(arguments_gc_map[i] == '1')
				{
					aptr = atemp;
					*(char **)aptr = Copy(*(char **)aptr);
					atemp = (char *)atemp + 4;
				}
			}
		}
		// locals
		int locals_gc_map = *((int *)previous + 3);

		void *lptr = (char *)previous + 12;
		void *temp = lptr;
		int j = 0;
		for(j=0; j<locals_gc_map; j++)
		{
			temp = (char *)((char *)temp + 4);//reference locals
			lptr = temp;
			*(char **)lptr = (char *)Copy(*(char **)((char *)lptr + 4 * j));
		}
		previous = (char *)(*((char **)previous));
	}

	while(heap.toStart < heap.toNext)
	{
		int size;
		void *obj = heap.toStart;
		int isObjOrArray = *((int *)obj + 1);
		if(isObjOrArray == 1)// if it is array
		{
			size = *((unsigned *)obj + 2) * 4 + 16;
			heap.toStart += size;
			continue ;
		}

		void *vptr = *((char **)obj);
		char *field_gc_map = (char *)(*((char **)vptr));
		int len = strlen(field_gc_map);
		size = 16 + 4 * len;
		int k = 0;
		obj = (char *)obj + 12;
		for(k=0; k<len; k++)
		{
			obj = (char *)obj + 4;
			if(field_gc_map[k] == '1')// if the field is reference
			{
				void *p = *((char **)obj);
				*((char **)obj) = (char *)Copy(p);
			}
		}
		heap.toStart += size;
	}

	char *swap = heap.from;
	heap.from = heap.to;
	heap.to = swap;

	heap.fromFree = heap.toNext;
}