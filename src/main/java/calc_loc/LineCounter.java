package calc_loc;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.comments.Comment;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class LineCounter {
    int lineOfComments;
    int numberOfComments;
    int lineOfCode;

    public LineCounter(CompilationUnit cu)
    {
        lineOfComments=0;
        numberOfComments=0;

        lineOfCode=cu.getRange().get().end.line-cu.getRange().get().begin.line+1;
        Map<Integer, Integer>commentMap=new TreeMap<>();

        List<Comment> comments=cu.getAllContainedComments();
        for(Comment comment : comments)
            commentMap.put(comment.getRange().get().begin.line, comment.getRange().get().end.line);

        for(Integer i : commentMap.keySet())
        {
            numberOfComments++;
            lineOfComments+=commentMap.get(i)-i+1;
        }
    }

    public int getLineOfComments() {
        return lineOfComments;
    }
    public int getNumberOfComments() {
        return numberOfComments;
    }
    public int getLineOfCodes()
    {
        return lineOfCode;
    }
}
