package com.community.demo.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QBoard is a Querydsl query type for Board
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QBoard extends EntityPathBase<Board> {

    private static final long serialVersionUID = -1608208012L;

    public static final QBoard board = new QBoard("board");

    public final QTimeBase _super = new QTimeBase(this);

    public final NumberPath<Long> bno = createNumber("bno", Long.class);

    public final NumberPath<Integer> cmtQty = createNumber("cmtQty", Integer.class);

    public final StringPath content = createString("content");

    public final ListPath<File, QFile> fileList = this.<File, QFile>createList("fileList", File.class, QFile.class, PathInits.DIRECT2);

    public final NumberPath<Integer> fileQty = createNumber("fileQty", Integer.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modDate = _super.modDate;

    public final NumberPath<Integer> readCount = createNumber("readCount", Integer.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> regDate = _super.regDate;

    public final StringPath title = createString("title");

    public final StringPath writer = createString("writer");

    public QBoard(String variable) {
        super(Board.class, forVariable(variable));
    }

    public QBoard(Path<? extends Board> path) {
        super(path.getType(), path.getMetadata());
    }

    public QBoard(PathMetadata metadata) {
        super(Board.class, metadata);
    }

}

