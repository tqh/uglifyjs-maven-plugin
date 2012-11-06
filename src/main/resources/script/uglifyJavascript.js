uglifyJavascript = function (code) {
  var toplevel_ast = UglifyJS.parse(code);
  toplevel_ast.figure_out_scope();
  var compressor = UglifyJS.Compressor();
  var compressed_ast = toplevel_ast.transform(compressor);
  compressed_ast.figure_out_scope();
  compressed_ast.compute_char_frequency();
  compressed_ast.mangle_names();
  return compressed_ast.print_to_string();
};
