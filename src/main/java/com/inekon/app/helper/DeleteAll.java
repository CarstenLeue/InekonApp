package com.inekon.app.helper;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

public class DeleteAll extends SimpleFileVisitor<Path> {

	public static final DeleteAll SINGLETON = new DeleteAll();

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.nio.file.SimpleFileVisitor#visitFile(java.lang.Object,
	 * java.nio.file.attribute.BasicFileAttributes)
	 */
	@Override
	public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
		Files.delete(file);
		return FileVisitResult.CONTINUE;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.nio.file.SimpleFileVisitor#postVisitDirectory(java.lang.Object,
	 * java.io.IOException)
	 */
	@Override
	public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
		Files.delete(dir);
		return FileVisitResult.CONTINUE;
	}

}
